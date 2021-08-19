# Copyright (c) 2017 Wind River Systems, Inc.
# Copyright (c) 2017-2018 VMware, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import os
import sys
import platform
import yaml
from logging import config as log_config

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# Quick-start development settings - unsuitable for production
# See https://docs.djangoproject.com/en/1.9/howto/deployment/checklist/

# SECURITY WARNING: keep the secret key used in production secret!
SECRET_KEY = '3o-wney!99y)^h3v)0$j16l9=fdjxcb+a8g+q3tfbahcnu2b0o'

# SECURITY WARNING: don't run with debug turned on in production!
# DEBUG = True

ALLOWED_HOSTS = ['*']

# Application definition

INSTALLED_APPS = [
    'django.contrib.auth',
    'django.contrib.contenttypes',
    'django.contrib.sessions',
    'django.contrib.messages',
    'django.contrib.staticfiles',
    'rest_framework',
    'multivimbroker.pub.database',
]

MIDDLEWARE_CLASSES = [
    'django.middleware.security.SecurityMiddleware',
    'django.contrib.sessions.middleware.SessionMiddleware',
    'django.middleware.common.CommonMiddleware',
    'django.middleware.csrf.CsrfViewMiddleware',
    'django.contrib.auth.middleware.AuthenticationMiddleware',
    'django.contrib.auth.middleware.SessionAuthenticationMiddleware',
    'django.contrib.messages.middleware.MessageMiddleware',
    'django.middleware.clickjacking.XFrameOptionsMiddleware',
]

ROOT_URLCONF = 'multivimbroker.urls'

WSGI_APPLICATION = 'multivimbroker.wsgi.application'

REST_FRAMEWORK = {
    'DEFAULT_RENDERER_CLASSES': (
        'rest_framework.renderers.JSONRenderer',
    ),

    'DEFAULT_PARSER_CLASSES': (
        'rest_framework.parsers.JSONParser',
        'rest_framework.parsers.MultiPartParser',
        # 'rest_framework.parsers.FormParser',
        # 'rest_framework.parsers.FileUploadParser',
    )
}


# CACHE_BACKEND = 'redis_cache.cache://%s@%s:%s' %
# (REDIS_PASSWD, REDIS_HOST, REDIS_PORT)

TIME_ZONE = 'UTC'

# Static files (CSS, JavaScript, Images)
# https://docs.djangoproject.com/en/1.6/howto/static-files/

STATIC_URL = '/static/'

if platform.system() == 'Windows' or 'test' in sys.argv:
    LOGGING = {
        'version': 1,
        'disable_existing_loggers': True,
        'formatters': {
            'standard': {
                'format': '%(asctime)s:[%(name)s]:[%(filename)s]'
                +'-[%(lineno)d] [%(levelname)s]:%(message)s',
            },
        },
        'filters': {
        },
        'handlers': {
            'file_handler': {
                'level': 'DEBUG',
                'class': 'logging.handlers.RotatingFileHandler',
                'filename': os.path.join(BASE_DIR, 'logs/test.log'),
                'formatter': 'standard',
                'maxBytes': 1024 * 1024 * 50,
                'backupCount': 5,
            },
        },

        'loggers': {
            'common': {
                'handlers': ['file_handler'],
                'level': 'DEBUG',
                'propagate': False
            },
        }
    }
else:
    log_path = "/var/log/onap/multicloud/multivimbroker"
    if not os.path.exists(log_path):
        os.makedirs(log_path)

    LOGGING_CONFIG = None
    # yaml configuration of logging
    LOGGING_FILE = os.path.join(BASE_DIR, 'multivimbroker/pub/config/log.yml')
    with open(file=LOGGING_FILE, mode='r', encoding="utf-8")as file:
        logging_yaml = yaml.load(stream=file, Loader=yaml.FullLoader)
    log_config.dictConfig(config=logging_yaml)

if 'test' in sys.argv:
    # from multivimbroker.pub.config import config
    REST_FRAMEWORK = {}
    import platform

    if platform.system() == 'Linux':
        TEST_RUNNER = 'xmlrunner.extra.djangotestrunner.XMLTestRunner'
        TEST_OUTPUT_VERBOSE = True
        TEST_OUTPUT_DESCRIPTIONS = True
        TEST_OUTPUT_DIR = 'test-reports'
