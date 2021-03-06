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
package net.sourceforge.cilib.pso.velocityprovider;

import com.google.common.base.Supplier;
import net.sourceforge.cilib.algorithm.AbstractAlgorithm;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.math.random.GaussianDistribution;
import net.sourceforge.cilib.math.random.ProbabilityDistributionFuction;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.type.types.container.Vector;
import net.sourceforge.cilib.util.Sequence;

public final class RandomNearbyVelocityProvider implements VelocityProvider {

    private static final long serialVersionUID = 8204479765311251730L;
    
    private ProbabilityDistributionFuction random;

    /** Creates a new instance of StandardVelocityUpdate. */
    public RandomNearbyVelocityProvider() {
        this.random = new GaussianDistribution();
    }

    /**
     * Copy constructor.
     * @param copy The object to copy.
     */
    public RandomNearbyVelocityProvider(RandomNearbyVelocityProvider copy) {
        this.random = copy.random;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RandomNearbyVelocityProvider getClone() {
        return new RandomNearbyVelocityProvider(this);
    }

    /**
     * Perform the velocity update for the given <tt>Particle</tt>.
     * @param particle The Particle velocity that should be updated.
     */
    @Override
    public Vector get(Particle particle) {
        Topology<Particle> topology = ((PSO) AbstractAlgorithm.get()).getTopology();
        Vector average = Vector.copyOf(Sequence.repeat(0.0, particle.getDimension()));
        
        for(Particle p : topology) {
            average = average.plus((Vector) p.getVelocity());
        }
        
        average = average.divide(topology.size());
        
        average.multiply(new Supplier<Number>() {
            @Override
            public Number get() {
                return random.getRandomNumber();
            }
        });     
        
        return average;
    }

    public void setRandom(ProbabilityDistributionFuction random) {
        this.random = random;
    }

    public ProbabilityDistributionFuction getRandom() {
        return random;
    }
}
