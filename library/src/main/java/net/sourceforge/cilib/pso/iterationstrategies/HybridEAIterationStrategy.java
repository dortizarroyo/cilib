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
package net.sourceforge.cilib.pso.iterationstrategies;

import com.google.common.collect.Lists;
import java.util.List;
import net.sourceforge.cilib.algorithm.population.AbstractIterationStrategy;
import net.sourceforge.cilib.controlparameter.ConstantControlParameter;
import net.sourceforge.cilib.controlparameter.ControlParameter;
import net.sourceforge.cilib.entity.Entity;
import net.sourceforge.cilib.entity.EntityType;
import net.sourceforge.cilib.entity.Particle;
import net.sourceforge.cilib.entity.Topologies;
import net.sourceforge.cilib.entity.Topology;
import net.sourceforge.cilib.entity.comparator.SocialBestFitnessComparator;
import net.sourceforge.cilib.entity.operators.CrossoverOperator;
import net.sourceforge.cilib.entity.operators.crossover.real.BlendCrossoverStrategy;
import net.sourceforge.cilib.pso.PSO;
import net.sourceforge.cilib.util.selection.Samples;
import net.sourceforge.cilib.util.selection.recipes.ElitistSelector;
import net.sourceforge.cilib.util.selection.recipes.RouletteWheelSelector;
import net.sourceforge.cilib.util.selection.recipes.Selector;
import net.sourceforge.cilib.util.selection.weighting.CurrentFitness;
import net.sourceforge.cilib.util.selection.weighting.EntityWeighting;

/**
 * ﻿@article {springerlink:10.1007/s10015-010-0846-z,
 *  author = {Duong, Sam and Kinjo, Hiroshi and Uezato, Eiho and Yamamoto, Tetsuhiko},
 *  affiliation = {Faculty of Engineering, University of the Ryukyus, 1 Senbaru, Nishihara, Okinawa, 903-0213 Japan},
 *  title = {Particle swarm optimization with genetic recombination: a hybrid evolutionary algorithm},
 *  journal = {Artificial Life and Robotics},
 *  publisher = {Springer Japan},
 *  issn = {1433-5298},
 *  keyword = {Computer Science},
 *  pages = {444-449},
 *  volume = {15},
 *  issue = {4},
 *  url = {http://dx.doi.org/10.1007/s10015-010-0846-z},
 *  note = {10.1007/s10015-010-0846-z},
 *  year = {2010}
 *  }
 */
public class HybridEAIterationStrategy extends AbstractIterationStrategy<PSO> {
    
    private CrossoverOperator crossover;
    private ControlParameter numberOfOffspring;
    private Selector selector;
    
    public HybridEAIterationStrategy() {
        BlendCrossoverStrategy cs = new BlendCrossoverStrategy();
        cs.setAlpha(ConstantControlParameter.of(0.4));
        
        this.crossover = new CrossoverOperator();
        this.crossover.setSelectionStrategy(new RouletteWheelSelector(new EntityWeighting(new CurrentFitness<Entity>())));
        this.crossover.setCrossoverProbability(ConstantControlParameter.of(0.1));
        this.crossover.setCrossoverStrategy(cs);
        
        this.numberOfOffspring = ConstantControlParameter.of(20);
        this.selector = new ElitistSelector();
    }
    
    public HybridEAIterationStrategy(HybridEAIterationStrategy copy) {
        this.crossover = copy.crossover.getClone();
        this.numberOfOffspring = copy.numberOfOffspring.getClone();
        this.selector = copy.selector;
    }

    @Override
    public HybridEAIterationStrategy getClone() {
        return new HybridEAIterationStrategy(this);
    }

    @Override
    public void performIteration(PSO algorithm) {
        Topology<Particle> topology = algorithm.getTopology();
        int size = topology.size();
        
        // pos/vel update
        for (Particle current : topology) {
            current.updateVelocity();
            current.updatePosition();

            boundaryConstraint.enforce(current);
            current.calculateFitness();
        }
        
        // crossover
        List<Particle> offspring = Lists.newArrayList();
        for (int i = 0; i < numberOfOffspring.getParameter(); i++) {
             offspring.addAll(crossover.crossover(topology));
        }
        
        for (Particle p : offspring) {
            p.getProperties().put(EntityType.Particle.BEST_POSITION, p.getCandidateSolution());
            p.setNeighbourhoodBest(p);
            
            topology.add(p);
        }
        
        // calculate fitness
        for (Particle p : topology) {
            p.calculateFitness();
        }
        
        // rank and eliminate
        Topology<Particle> newTopology = topology.getClone();
        topology.clear();
        topology.addAll(selector.on(newTopology).select(Samples.first(size)));
        
        // selector removes nbests
        for (Particle p : topology) {
            Particle nBest = Topologies.getNeighbourhoodBest(topology, p, new SocialBestFitnessComparator());
            p.setNeighbourhoodBest(nBest);
        }
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setNumberOfOffspring(ControlParameter numberOfOffspring) {
        this.numberOfOffspring = numberOfOffspring;
    }

    public ControlParameter getNumberOfOffspring() {
        return numberOfOffspring;
    }

    public void setCrossoverStrategy(CrossoverOperator crossoverStrategy) {
        this.crossover = crossoverStrategy;
    }

    public CrossoverOperator getCrossoverStrategy() {
        return crossover;
    }    
}
