# Copyright (c) 2017-2018 VMware, Inc.
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at:
#       http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

import mock
import os
import unittest

from multivimbroker.pub.utils import fileutil


class TestFileutil(unittest.TestCase):

    @mock.patch.object(os.path, "exists")
    @mock.patch("os.makedirs")
    def test_make_dirs_path_exists(self, mock_mkdir, mock_exists):
        new_path = "/tmp/test"
        mock_exists.return_value = True
        fileutil.make_dirs(new_path)
        mock_mkdir.assert_not_called()

    @mock.patch.object(os.path, "exists")
    @mock.patch("os.makedirs")
    def test_make_dirs_path_not_exists(self, mock_mkdir, mock_exists):
        new_path = "/tmp/test"
        mock_exists.return_value = False
        fileutil.make_dirs(new_path)
        mock_mkdir.assert_called_once_with(new_path, 0o777)

    @mock.patch.object(os.path, "exists")
    @mock.patch("shutil.rmtree")
    def test_delete_dirs_success(self, mock_rmtree, mock_exists):
        mock_exists.return_value = True
        new_path = "/tmp/tests"
        fileutil.delete_dirs(new_path)
        mock_rmtree.assert_called_once_with(new_path)

    @mock.patch.object(os.path, "exists")
    @mock.patch("shutil.rmtree")
    def test_delete_dirs_failed(self, mock_rmtree, mock_exists):
        mock_exists.return_value = True
        mock_rmtree.side_effect = [Exception("Fake exception")]
        new_path = "/tmp/tests"
        fileutil.delete_dirs(new_path)
        mock_rmtree.assert_called_once_with(new_path)

    @mock.patch.object(fileutil, "make_dirs")
    @mock.patch("urllib.request.urlopen")
    def test_download_file_from_http_success(self, mock_urlopen, mock_mkdir):
        url = "http://www.example.org/test.dat"
        local_dir = "/tmp/"
        file_name = "test.dat"
        mock_req = mock.Mock()
        mock_req.read.return_value = "hello world".encode()
        mock_urlopen.return_value = mock_req
        m = mock.mock_open()
        expect_ret = (True, "/tmp/test.dat")
        with mock.patch('{}.open'.format(__name__), m, create=True):
            ret = fileutil.download_file_from_http(url, local_dir, file_name)
            self.assertEqual(expect_ret, ret)

    @mock.patch.object(fileutil, "make_dirs")
    @mock.patch("urllib.request.urlopen")
    def test_download_file_from_http_fail(self, mock_urlopen, mock_mkdir):
        url = "http://www.example.org/test.dat"
        local_dir = "/tmp/"
        file_name = "test.dat"
        mock_req = mock.Mock()
        mock_req.read.return_value = "hello world".encode()
        mock_urlopen.side_effect = [Exception("fake exception")]
        expect_ret = (False, "/tmp/test.dat")
        ret = fileutil.download_file_from_http(url, local_dir, file_name)
        self.assertEqual(expect_ret, ret)
