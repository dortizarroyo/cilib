/**
 * Computational Intelligence Library (CIlib)
 * Copyright (C) 2003 - 2010
 * Computational Intelligence Research Group (CIRG@UP)
 * Department of Computer Science
 * University of Pretoria
 * South Africa
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.cilib.pso.particle;

import static org.junit.Assert.*;
import net.sourceforge.cilib.entity.Particle;

import org.junit.Test;

public class StandardParticleTest {

    @Test
    public void equals() {
        Particle p1 = new StandardParticle();
        Particle p2 = new StandardParticle();

        p1.setNeighbourhoodBest(p1);
        p2.setNeighbourhoodBest(p1);

        assertFalse(p1.equals(p2));
        assertFalse(p1.hashCode() == p2.hashCode());
        assertFalse(p1.equals(null));
    }

    @Test
    public void hashCodes() {
        Particle p1 = new StandardParticle();
        Particle p2 = new StandardParticle();

        assertTrue(p1.hashCode() != p2.hashCode());
    }

}
