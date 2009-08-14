/*
 * SpecializedProduction.cs
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

using System.Collections.Generic;

namespace PerCederberg.Grammatica.Runtime {

    /**
     * This class simply exists to ease the job of the Parser adding the
     * right nodes to the right places, if someone uses --specialize.
     *
     * @author   Connor Prussin, <cprussin at vt dot edu>
     * @version  1.0
     * @since    1.6
     */
    public class SpecializedProduction : Production {

        /**
         * The list of index locations.  Utilized in well-named accessors.
         */
        protected List<int> elementIndices;

        /**
         * Creates a new specialized production node.
         *
         * @param pattern        the production pattern
         */
        public SpecializedProduction(ProductionPatternAlternative alt)
        	: base(alt) {
            elementIndices = new List<int>();
            elementIndices.Add(0);
        }

        /**
         * The elementIndices list property (read-only).
         */
        public List<int> ElementIndices {
            get {
                return elementIndices;
            }
        }
    }
}
