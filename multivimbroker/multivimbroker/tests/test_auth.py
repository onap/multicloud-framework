import base64
import json
import os
import subprocess
import sys
import tempfile
from unittest.mock import patch

from django.core.exceptions import ImproperlyConfigured
from django.http import HttpResponse
from django.test import Client, SimpleTestCase, override_settings
from django.urls import re_path
from rest_framework.decorators import api_view
from rest_framework.response import Response
from rest_framework.views import APIView

from multivimbroker.auth import load_auth_config
from multivimbroker.forwarder.base import BaseHandler
from multivimbroker.pub.utils import restcall
from multivimbroker.pub.utils.syscomm import originHeaders


def authorization(username='operator', password='secret'):
    encoded = base64.b64encode(
        (username + ':' + password).encode('utf-8')).decode('ascii')
    return 'Basic ' + encoded


def plain_view(request):
    return HttpResponse('ok')


@api_view(['GET', 'POST'])
def drf_view(request):
    return Response({'status': 'ok'})


urlpatterns = [
    re_path(r'^api/multicloud/v0/plain$', plain_view),
    re_path(r'^api/multicloud/v1/drf$', drf_view),
    re_path(r'^api/multicloud/v0/swagger.json/?$', plain_view),
]


@override_settings(ROOT_URLCONF=__name__)
class TestAuthentication(SimpleTestCase):
    def test_enabled_rejects_anonymous_drf_and_plain_requests(self):
        users = (('operator', 'secret'),)
        with override_settings(MULTICLOUD_AUTH_ENABLED=True,
                               MULTICLOUD_AUTH_PASSWORDS=users):
            for path in ('/api/multicloud/v0/plain',
                         '/api/multicloud/v1/drf'):
                response = Client().get(path)
                self.assertEqual(401, response.status_code)
                self.assertEqual('Basic realm="multicloud", '
                                 'charset="UTF-8"',
                                 response['WWW-Authenticate'])

    @override_settings(MULTICLOUD_AUTH_ENABLED=True,
                       MULTICLOUD_AUTH_PASSWORDS=(
                           ('operator', 'secret'),
                           ('second', 'p\u00e4ss:word')),
                       REST_FRAMEWORK={'DEFAULT_AUTHENTICATION_CLASSES': []})
    @patch.object(drf_view.cls, 'authentication_classes', [])
    def test_both_deployment_users_pass_without_database_users(self):
        for username, password in (('operator', 'secret'),
                                   ('second', 'p\u00e4ss:word')):
            for path in ('/api/multicloud/v0/plain',
                         '/api/multicloud/v1/drf'):
                response = Client().get(
                    path, HTTP_AUTHORIZATION=authorization(username, password))
                self.assertEqual(200, response.status_code)

    @override_settings(MULTICLOUD_AUTH_ENABLED=True,
                       MULTICLOUD_AUTH_PASSWORDS=(('operator', 'secret'),))
    def test_invalid_headers_are_challenged(self):
        for header in ('', 'Bearer token', 'Basic', 'Basic !!!',
                       'Basic /w==', 'Basic bm9jb2xvbg==',
                       authorization('operator', 'wrong'),
                       authorization('unknown', 'secret'),
                       authorization() + ' extra'):
            response = Client().get('/api/multicloud/v0/plain',
                                    HTTP_AUTHORIZATION=header)
            self.assertEqual(401, response.status_code)
            self.assertIn('Basic', response['WWW-Authenticate'])
            self.assertNotIn(b'secret', response.content)

    @override_settings(MULTICLOUD_AUTH_ENABLED=True,
                       MULTICLOUD_AUTH_PASSWORDS=(('operator', 'secret'),))
    def test_only_exact_swagger_get_is_anonymous(self):
        path = '/api/multicloud/v0/swagger.json'
        self.assertEqual(200, Client().get(path + '?probe=true').status_code)
        for method in ('post', 'put', 'patch', 'delete', 'head', 'options'):
            self.assertEqual(401, getattr(Client(), method)(path).status_code)
        for near_match in (path + '/', path + '/child', path + '.json',
                           '/api/multicloud/v1/swagger.json', '/health', '/'):
            self.assertEqual(401, Client().get(near_match).status_code)

    @override_settings(MULTICLOUD_AUTH_ENABLED=False)
    def test_disabled_preserves_anonymous_access(self):
        for path in ('/api/multicloud/v0/plain', '/api/multicloud/v1/drf'):
            self.assertEqual(200, Client().get(path).status_code)

    @override_settings(ROOT_URLCONF='multivimbroker.urls',
                       MULTICLOUD_AUTH_ENABLED=True,
                       MULTICLOUD_AUTH_PASSWORDS=(('operator', 'secret'),))
    def test_real_api_routes_and_localhost_are_protected(self):
        paths = ('/api/multicloud/v0/vim_types',
                 '/api/multicloud/v0/check_vim_capacity',
                 '/api/multicloud/v0/owner_region/identity/v3',
                 '/api/multicloud/v0/owner_region/registry',
                 '/api/multicloud/v0/owner_region/extensions',
                 '/api/multicloud/v0/owner_region/servers',
                 '/api/multicloud/v1/vim_types',
                 '/api/multicloud/v1/check_vim_capacity',
                 '/api/multicloud/v1/owner/region/registry',
                 '/api/multicloud/v1/owner/region/infra_workload',
                 '/api/multicloud/v1/owner/region/servers')
        for path in paths:
            for method in ('get', 'post', 'put', 'patch', 'delete', 'head',
                           'options'):
                self.assertEqual(401, getattr(Client(), method)(
                    path, REMOTE_ADDR='127.0.0.1').status_code)


class TestConfiguration(SimpleTestCase):
    def load(self, contents, **environment):
        with tempfile.NamedTemporaryFile(mode='w', encoding='utf-8') as users:
            users.write(contents)
            users.flush()
            values = {'MULTICLOUD_AUTH_ENABLED': 'true',
                      'MULTICLOUD_AUTH_USERS_FILE': users.name}
            values.update(environment)
            with patch.dict(os.environ, values, clear=True):
                return load_auth_config()

    def test_default_and_explicit_disabled_do_not_require_a_file(self):
        for values in ({}, {'MULTICLOUD_AUTH_ENABLED': 'false',
                            'MULTICLOUD_AUTH_USERS_FILE': '/missing'}):
            with patch.dict(os.environ, values, clear=True):
                self.assertEqual((False, ()), load_auth_config())

    def test_valid_users_preserve_utf8_and_password_colons(self):
        entries = [{'username': 'operator', 'password': 'secret'},
                   {'username': 'second', 'password': 'p\u00e4ss:word'}]
        users = (('operator', 'secret'), ('second', 'p\u00e4ss:word'))
        self.assertEqual((True, users), self.load(json.dumps(entries)))

    def test_invalid_flags_fail_closed_without_echo(self):
        for value in ('', '1', 'yes', 'tru', 'secret'):
            with patch.dict(os.environ, {'MULTICLOUD_AUTH_ENABLED': value},
                            clear=True):
                with self.assertRaises(ImproperlyConfigured) as raised:
                    load_auth_config()
                self.assertNotIn('secret', str(raised.exception))

    def test_missing_or_unreadable_file_fails_without_echo(self):
        for values in ({'MULTICLOUD_AUTH_ENABLED': 'true'},
                       {'MULTICLOUD_AUTH_ENABLED': 'true',
                        'MULTICLOUD_AUTH_USERS_FILE': '/missing/secret'}):
            with patch.dict(os.environ, values, clear=True):
                with self.assertRaises(ImproperlyConfigured) as raised:
                    load_auth_config()
                self.assertNotIn('secret', str(raised.exception))

    def test_invalid_user_files_fail_without_echo(self):
        invalid = ('secret', '{}', '[]', 'null', '[null]', '[1]',
                   '[{"username":"secret","password":""}]',
                   '[{"username":"","password":"secret"}]',
                   '[{"username":"  ","password":"secret"}]',
                   '[{"username":"secret","password":"  "}]',
                   '[{"username":"bad:secret","password":"secret"}]',
                   '[{"username":1,"password":"secret"}]',
                   '[{"username":"secret","password":1}]',
                   '[{"username":"secret","password":"secret"},'
                   '{"username":"secret","password":"other"}]',
                   '[{"username":"secret"}]',
                   '[{"username":"secret","password":"\\ud800"}]')
        for contents in invalid:
            with self.assertRaises(ImproperlyConfigured) as raised:
                self.load(contents)
            self.assertNotIn('secret', str(raised.exception))

    def test_enabled_pecan_cannot_bypass_wsgi_authentication(self):
        with self.assertRaises(ImproperlyConfigured):
            self.load('[{"username":"operator","password":"secret"}]',
                      WEB_FRAMEWORK='pecan')

    def test_diagnostic_settings_and_auth_repr_redact_credentials(self):
        username = 'diagnostic-operator'
        password = 'diagnostic-password:sentinel'
        header = authorization(username, password)
        secrets = (username, password, username + ':' + password,
                   header, header.split()[1])
        with tempfile.NamedTemporaryFile(mode='w') as users:
            json.dump([{'username': username, 'password': password}], users)
            users.flush()
            environment = dict(
                os.environ, MULTICLOUD_AUTH_ENABLED='true',
                MULTICLOUD_AUTH_USERS_FILE=users.name,
                DJANGO_SETTINGS_MODULE='multivimbroker.settings')
            code = (
                'import sys; sys.argv.append("test"); '
                'from django.views.debug import SafeExceptionReporterFilter; '
                'from multivimbroker.auth import BasicAuthMiddleware; '
                'middleware = BasicAuthMiddleware(lambda request: None); '
                'diagnostics = repr(SafeExceptionReporterFilter()'
                '.get_safe_settings()) + repr(middleware) '
                '+ repr(vars(middleware)) + repr(vars(BasicAuthMiddleware)); '
                'assert all(value not in diagnostics for value in '
                + repr(secrets) + ')')
            result = subprocess.run([sys.executable, '-c', code],
                                    env=environment, stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE)
        self.assertEqual(0, result.returncode, result.stderr.decode())

    def test_real_wsgi_startup_and_drf_use_deployment_credentials(self):
        with tempfile.NamedTemporaryFile(mode='w') as users:
            json.dump([{'username': 'operator', 'password': 'secret'}], users)
            users.flush()
            environment = dict(os.environ)
            environment.update(
                MULTICLOUD_AUTH_ENABLED='true',
                MULTICLOUD_AUTH_USERS_FILE=users.name,
                DJANGO_SETTINGS_MODULE='multivimbroker.settings')
            code = (
                'import sys; sys.argv.append("test"); '
                'from multivimbroker.wsgi import application; '
                'from django.test import Client; '
                'client = Client(); '
                'path = "/api/multicloud/v0/vim_types"; '
                'assert client.get(path).status_code '
                '== 401; '
                'assert client.get("/api/multicloud/v1/vim_types", '
                'HTTP_AUTHORIZATION="Basic b3BlcmF0b3I6c2VjcmV0")'
                '.status_code == 200')
            result = subprocess.run([sys.executable, '-c', code],
                                    env=environment, stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE)
            self.assertEqual(0, result.returncode, result.stderr.decode())
            environment['MULTICLOUD_AUTH_USERS_FILE'] = '/missing/secret'
            result = subprocess.run([sys.executable, '-c', code],
                                    env=environment, stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE)
            self.assertNotEqual(0, result.returncode)
            self.assertNotIn(b'/missing/secret', result.stderr)

    def test_container_launcher_rejects_bad_config_before_starting(self):
        environment = dict(os.environ, MULTICLOUD_AUTH_ENABLED='invalid')
        launcher = os.path.abspath('run.sh')
        with tempfile.TemporaryDirectory(dir='.') as directory:
            environment['PYTHONPATH'] = os.getcwd()
            result = subprocess.run(['sh', launcher], env=environment,
                                    cwd=directory, stdout=subprocess.PIPE,
                                    stderr=subprocess.PIPE,
                                    timeout=10)
        self.assertNotEqual(0, result.returncode)
        self.assertIn(b'MULTICLOUD_AUTH_ENABLED', result.stderr)


@override_settings(ROOT_URLCONF='multivimbroker.urls',
                   MULTICLOUD_AUTH_ENABLED=True,
                   MULTICLOUD_AUTH_PASSWORDS=(('operator', 'secret'),),
                   REST_FRAMEWORK={'DEFAULT_AUTHENTICATION_CLASSES': []})
class TestForwardingAuthentication(SimpleTestCase):
    @patch.object(APIView, 'authentication_classes', [])
    @patch('multivimbroker.pub.utils.restcall.httplib2.Http.request')
    @patch('multivimbroker.forwarder.base.getMultivimDriver')
    def test_normal_and_multipart_proxy_preserve_authorization(
            self, driver, transport):
        driver.return_value = '/api/multicloud-plugin/v0/owner_region/servers'
        transport.return_value = ({'status': '200'}, b'{}')
        for content_type, body in (('application/json', b'{"name":"test"}'),
                                   ('multipart/form-data; boundary=auth',
                                    b'--auth\r\ncontent\r\n--auth--\r\n')):
            response = Client().post(
                '/api/multicloud/v0/owner_region/servers', data=body,
                content_type=content_type,
                HTTP_AUTHORIZATION=authorization())
            self.assertEqual(200, response.status_code)
            headers = transport.call_args[1]['headers']
            self.assertEqual(authorization(), headers['AUTHORIZATION'])
            self.assertEqual(content_type, headers['CONTENT-TYPE'])
            self.assertEqual(body, transport.call_args[1]['body'])

    @patch('multivimbroker.pub.utils.restcall.urllib.request.urlopen')
    def test_multipart_transport_preserves_authorization(self, transport):
        request = Client().request().wsgi_request
        request.META['HTTP_AUTHORIZATION'] = authorization()
        transport.return_value.code = '200'
        transport.return_value.read.return_value = b'{}'
        response = BaseHandler()._request(
            '/api/multicloud-plugin/upload', 'POST', b'chart',
            originHeaders(request), multipart=True)
        self.assertEqual(200, response.status_code)
        outbound = transport.call_args[0][0]
        self.assertEqual(authorization(), outbound.get_header('Authorization'))
        self.assertEqual(b'chart', outbound.data)

    @patch('multivimbroker.pub.utils.restcall.httplib2.Http.request')
    @patch.object(restcall, 'AAI_USERNAME', 'aai-user')
    @patch.object(restcall, 'AAI_PASSWORD', 'aai-password')
    def test_aai_authentication_remains_separate(self, transport):
        transport.return_value = ({'status': '200'}, b'{}')
        restcall.get_res_from_aai('/cloud-regions')
        headers = transport.call_args[1]['headers']
        expected = 'Basic YWFpLXVzZXI6YWFpLXBhc3N3b3Jk'
        self.assertEqual(expected, headers['Authorization'])
