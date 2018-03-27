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
        mock_mkdir.assert_called_once_with(new_path, 0777)

    @mock.patch.object(os.path, "exists")
    @mock.patch("shutil.rmtree")
    def test_delete_dirs_success(self, mock_rmtree, mock_exists):
        mock_exists.return_value = True
        new_path = "/tmp/tests"
        fileutil.delete_dirs(new_path)
        mock_rmtree.assert_called_once_with(new_path)
