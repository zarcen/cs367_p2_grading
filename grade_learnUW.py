#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os, sys, argparse, time
import shutil
import subprocess
import errno
from shell import execute
from timeout import timeout, TimeoutError

def make_student_dir(submission_dir):
    root, dirs, files = os.walk(submission_dir, topdown=True).next()
    files_map = {}
    for name in files:
        if ' - ' in name:
            toks = name.split(' - ')
            student_name = toks[1].strip()
            submit_datetimestr = toks[2].strip()
            file_name = toks[3].strip()
            date = submit_datetimestr.split(',')[0]
            if len(date.split(' ')[1]) == 1:
                # zero-padding for the day of month
                date = date.split(' ')[0] + ' 0' + date.split(' ')[1]
            timestr = submit_datetimestr.split(',')[1].strip()
            timetoks = timestr.split(' ')
            timestr = timetoks[1] + timetoks[2]
            if len(timetoks[1]) == 3:
                timestr = '0' + timetoks[1] + timetoks[2]
            filedate = time.strptime(date + "/" + timestr, "%b %d/%I%M%p")
            student_dir = os.path.join(submission_dir, student_name)
            if not os.path.exists(student_dir):
                os.makedirs(student_dir)
            if files_map.has_key(student_name + file_name):
                print 'Student %s has duplicate files submitted: %s' % (student_name, file_name)
                print 'Here leaves the latest one. Check the original files.' 
                if filedate > files_map[student_name + file_name]:
                    shutil.move(os.path.join(root, name), os.path.join(student_dir, file_name))
            else:
                files_map[student_name + file_name] = filedate
                shutil.move(os.path.join(root, name), os.path.join(student_dir, file_name))

def execute_testing_programs(submission_dir):
    src_dir = os.path.dirname(os.path.realpath(__file__))
    cp_files = ['ArrayListLoop.java', 'DblListnode.java', 'LinkedLoopTester.java', 'Loop.java']
    for root, dirs, files in os.walk(submission_dir, topdown=True):
        for student_dir in dirs:
            for f in cp_files:
                fp = os.path.join(src_dir, f)
                shutil.copy(fp, os.path.join(root, student_dir))
            testfiles_cp = "cp -f *.txt '%s'\n" % (os.path.join(root, student_dir))
            execute(testfiles_cp, verbose=True)
            execute_tester_in_student_dir(os.path.join(root, student_dir))

# Timeout the function if it lasts longer than 60 seconds
@timeout(60, os.strerror(errno.ETIMEDOUT))
def execute_tester_in_student_dir(student_dir):
    src_dir = os.path.dirname(os.path.realpath(__file__))
    compile_cmd = 'javac *.java >> grade.out 2>&1'
    run_cmd = 'java LinkedLoopTester >> grade.out 2>&1'
    # chdir to student's personal folder
    print 'change dir to %s' % student_dir
    os.chdir(student_dir)
    if os.path.exists('grade.out'):
        os.remove('grade.out')
    try:
        # compile
        execute(compile_cmd, verbose=True)
        # run the testing
        execute("echo '----> General Test' >> grade.out")
        execute(run_cmd, verbose=True)
    except TimeoutError:
        print "TimeoutError: check student\'s program"
        execute("echo 'TimeoutError: timeout when general testing' >> grade.out 2>&1")
    except subprocess.CalledProcessError as detail:
        print "CalledProcessError: %s" % detail

    try: 
        # run the testcases
        execute_testcases()
    finally:
        os.chdir(src_dir)

@timeout(60, os.strerror(errno.ETIMEDOUT))
def execute_testcases():
    testcases = ['java MessageLoopEditor < sampleInput.txt | diff - sampleOutput1.txt']
            
    for test_cmd in testcases:
        try:
            execute("echo '\n----> %s' >> grade.out" % test_cmd)
            execute("%s >> grade.out 2>&1" % test_cmd, verbose=True)
        except subprocess.CalledProcessError:
            # ignore
            pass
    return

    

def main():
    parser = argparse.ArgumentParser(description="""
        use this tool to do grading
        """)
    parser.add_argument("submission_dir", help="The submission_dir (the folder downloaded from Learn@UW)")
    parser.add_argument("-s", "--student", help="Re-run for a certain student's directory", default=None)
    parser.add_argument("-p", "--path", help="Enter the path of student's directory to run the grading process", action="store_true", default=None)
    args = parser.parse_args()
    if args.student is not None:
        execute_tester_in_student_dir(os.path.join(args.submission_dir, args.student))
    elif args.path is not None:
        execute_tester_in_student_dir(args.submission_dir)
    else:
        make_student_dir(args.submission_dir)
        execute_testing_programs(args.submission_dir)
                

if __name__ == '__main__':
    main()
