/*
 * TestAll.cs
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * As a special exception, the copyright holders of this library give
 * you permission to link this library with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the
 * license of that module. An independent module is a module which is
 * not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the
 * library, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 *
 * Copyright (c) 2003 Per Cederberg. All rights reserved.
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
