package com.liemily.neuralnetwork.layers;


import java.util.Arrays;

/**
 * Wrapper for node nodes, represented as a vector
 * @author Emily Li
 */
public class NeuronLayer {
    private double[] nodes;

    public NeuronLayer(int nodeSize) {
        nodes = new double[nodeSize];
        Arrays.fill(nodes, 1);
    }

    public int size() { return nodes.length; }

    public double[] getNodes() { return nodes; }

    public void setNodes(double[] nodes) {
        this.nodes = Arrays.copyOf(nodes, size());
    }

    @Override
    public String toString() {
        return "NeuronLayer{" +
                "nodes=" + Arrays.toString(nodes) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NeuronLayer that = (NeuronLayer) o;

        return Arrays.equals(nodes, that.nodes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(nodes);
    }
}
