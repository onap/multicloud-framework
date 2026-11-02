import base64
import binascii
import hashlib
import hmac
import json
import os

from django.conf import settings
from django.core.exceptions import ImproperlyConfigured
from django.http import HttpResponse


def load_auth_config():
    enabled = os.environ.get('MULTICLOUD_AUTH_ENABLED', 'false')
    enabled = enabled.strip().lower()
    if enabled not in ('true', 'false'):
        raise ImproperlyConfigured(
            'MULTICLOUD_AUTH_ENABLED must be true or false')
    if enabled == 'false':
        return False, ()
    if os.environ.get('WEB_FRAMEWORK') == 'pecan':
        raise ImproperlyConfigured(
            'MultiCloud authentication requires the Django WSGI application')
    path = os.environ.get('MULTICLOUD_AUTH_USERS_FILE', '')
    try:
        with open(path, encoding='utf-8') as users_file:
            entries = json.load(users_file)
    except (OSError, ValueError):
        raise ImproperlyConfigured(
            'MULTICLOUD_AUTH_USERS_FILE must be a readable JSON users array') \
            from None
    users = []
    usernames = set()
    if not isinstance(entries, list) or not entries:
        raise ImproperlyConfigured('MultiCloud authentication requires users')
    for entry in entries:
        username = entry.get('username') if isinstance(entry, dict) else None
        password = entry.get('password') if isinstance(entry, dict) else None
        if (not isinstance(username, str) or not username.strip()
                or ':' in username or username in usernames
                or not isinstance(password, str) or not password.strip()):
            raise ImproperlyConfigured(
                'Invalid MultiCloud authentication user')
        try:
            username.encode('utf-8')
            password.encode('utf-8')
        except UnicodeError:
            raise ImproperlyConfigured(
                'MultiCloud authentication users must be UTF-8') from None
        usernames.add(username)
        users.append((username, password))
    return True, tuple(users)


class BasicAuthMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response
        self.enabled = settings.MULTICLOUD_AUTH_ENABLED
        self.users = tuple((self.digest(username), self.digest(password))
                           for username, password
                           in settings.MULTICLOUD_AUTH_PASSWORDS)

    @staticmethod
    def digest(value):
        return hashlib.sha256(value.encode('utf-8')).digest()

    def __call__(self, request):
        if (not self.enabled or
                (request.method == 'GET' and request.path_info ==
                 '/api/multicloud/v0/swagger.json')):
            return self.get_response(request)
        authenticated = False
        try:
            scheme, token = request.META.get('HTTP_AUTHORIZATION', '').split()
            if scheme.lower() == 'basic':
                username, password = base64.b64decode(
                    token, validate=True).decode('utf-8').split(':', 1)
                username_hash = self.digest(username)
                password_hash = self.digest(password)
                for expected_user, expected_password in self.users:
                    user_matches = hmac.compare_digest(
                        username_hash, expected_user)
                    password_matches = hmac.compare_digest(
                        password_hash, expected_password)
                    authenticated |= user_matches & password_matches
        except (ValueError, UnicodeError, binascii.Error):
            pass
        if authenticated:
            return self.get_response(request)
        response = HttpResponse('Authentication required', status=401)
        response['WWW-Authenticate'] = (
            'Basic realm="multicloud", charset="UTF-8"')
        return response
