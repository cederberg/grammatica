/*
 * TestAll.cs
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307, USA.
 *
 * Copyright (c) 2003-2005 Per Cederberg. All rights reserved.
 */

using System;
using System.Reflection;

using PerCederberg.Grammatica.Test;

/**
 * A program for running all C# tests.
 *
 * @author   Per Cederberg, <per at percederberg dot net>
 * @version  1.0
 */
public class TestAll {

    /**
     * A program entry point to run this test suite stand-alone.
     *
     * @param args     the command-line arguments
     */
    public static void Main(string[] args) {
        int  failures = 0;

        // Run all tests
        failures += RunTests(new TestRegExp());
        failures += RunTests(new TestTokenizer());
        failures += RunTests(new TestProductionPattern());
        failures += RunTests(new TestRecursiveDescentParser());
        failures += RunTests(new TestArithmeticParser());
        failures += RunTests(new TestArithmeticCalculator());
        failures += RunTests(new TestRegexpParser());

        // Check for failures
        if (failures == 0) {
            Console.WriteLine("All tests succeeded");
        } else {
        	Console.Out.Flush();
            Console.Error.WriteLine("ERROR: failures in " + failures + " tests");
            Environment.Exit(failures);
        }
    }

    /**
     * Runs all the test methods in an object. The test methods are
     * found by reflection, and have names starting with "Test".
     *
     * @param obj            the object to test
     *
     * @return the number of failed tests
     */
    private static int RunTests(object obj) {
        Type          type = obj.GetType();
        BindingFlags  flags;
        MethodInfo[]  methods;
        int           failures = 0;

        Console.WriteLine("Running tests in " + type.Name +" class...");
        flags = BindingFlags.DeclaredOnly
              | BindingFlags.Public
              | BindingFlags.Instance;
        methods = type.GetMethods(flags);
        for (int i = 0; i < methods.Length; i++) {
            if (methods[i].Name.StartsWith("Test")) {
                failures += RunTest(obj, methods[i].Name);
            }
        }
        Console.WriteLine("Tests: " + methods.Length +
                          "  Failures: " + failures);
        Console.WriteLine();
        return failures;
    }

    /**
     * Runs a single test methods in an object. The test method is
     * executed via reflection.
     *
     * @param obj            the object instance
     * @param method         the test method name
     *
     * @return one (1) if the test failed, or
     *         zero (0) otherwise
     */
    private static int RunTest(object obj, string method) {
        Type          type = obj.GetType();
        BindingFlags  flags;

        flags = BindingFlags.Public
              | BindingFlags.Instance
              | BindingFlags.InvokeMethod;
        try {
            Console.Write("Testing " + method + "... ");
            type.InvokeMember(method, flags, null, obj, null);
            Console.WriteLine("ok");
            return 0;
        } catch (Exception e) {
            Console.WriteLine("FAILED!");
            Console.WriteLine(e.ToString());
            return 1;
        }
    }
}
