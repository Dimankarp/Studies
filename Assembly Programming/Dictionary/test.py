#!/usr/bin/python3

__unittest = True

import subprocess
import re
import unittest
import xmlrunner
from subprocess import CalledProcessError, Popen, PIPE


class IOLibraryTest(unittest.TestCase):

    def make(self, target):
         try:
            p = Popen(['make '+ target], shell=None, stdin=PIPE, stdout=PIPE)
            return p.returncode
         except CalledProcessError as exc:
            return exc.returncode

    def exist(self, fname):

        self.assertEqual(subprocess.call( ['test', '-f', fname] ), 0, "the tested program doesn't exist")

    def launch(self, fname, input, supress_err=False):
        self.exist(fname)
        output = b''
        try:
            p = Popen(['./'+fname], shell=None, stdin=PIPE, stdout=PIPE, stderr=None if not(supress_err) else subprocess.DEVNULL)
            (output, _) = p.communicate(input.encode())
            return (output.decode(), p.returncode)
        except CalledProcessError as exc:
            return (exc.output.decode(), exc.returncode)

    def test_contained_keys(self):
        inputs = ['D. Dontsova', 'I. Zhirkov', 'G. R. Martin', '\t', '\n\r\n\r']
        expected = ['Kulinarnaya kniga lentyaiki', 'Low Level Programming', 'The Song of Ice and Fire Antology',
                     'Tabularity and Singularity', 'The Whitespace']
        for i in range(len(inputs)):
            (output, code) = self.launch('main', inputs[i])
            self.assertEqual(expected[i], output, 'contained_keys(%s) returned wrong line: %s; expected: %s' % (inputs[i], output, expected[i]))
    def test_error_keys(self):
        inputs = ['D. dontsova', 'asbs', '11231', 'null', 'Don', 'I. Zhirko']
        for i in range(len(inputs)):
            (output, code) = self.launch('main', inputs[i], supress_err=True)
            self.assertEqual(code, 1, 'error_keys(%s) returned wrong return code: %d; expected: %d' % (inputs[i], code, 1))
    def test_long_but_legal_key(self):
        input = "A"*255
        expected = 'Long Novel'
        (output, code) = self.launch('main', input)
        self.assertEqual(expected, output, 'test_long_but_legal_key(A*255) returned wrong line: %s; expected: %s' %  (output, expected))

    def test_overly_long_key(self):
        input = "B"*256
        (output, code) = self.launch('main', input, supress_err=True)
        self.assertEqual(code, 1, 'test_overly_long_key(%s) returned wrong return code: %d; expected: %d' % (input, code, 1))

if __name__ == "__main__":
    with open('report.xml', 'w') as report:
        unittest.main(testRunner=xmlrunner.XMLTestRunner(output=report), failfast=False, buffer=False, catchbreak=False)
