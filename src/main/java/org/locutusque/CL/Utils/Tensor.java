package org.locutusque.CL.Utils;

import java.util.Random;

/**
 * If we need to train Machine Learning Models in Minecraft
 *
 */
class Tensor {
    private float[] data;
    private int[] shape;

    public Tensor(float[] data, int[] shape) {
        if (data.length != calculateSize(shape)) {
            throw new IllegalArgumentException("Invalid data size for given shape");
        }
        this.data = data;
        this.shape = shape;
    }
    public Tensor multiply(Tensor other) {
        checkMatrixMultiplicationCompatibility(other);

        int[] newShape = {shape[0], other.shape[1]};
        float[] resultData = new float[newShape[0] * newShape[1]];

        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < other.shape[1]; j++) {
                float sum = 0.0f;
                for (int k = 0; k < shape[1]; k++) {
                    sum += data[i * shape[1] + k] * other.data[k * other.shape[1] + j];
                }
                resultData[i * newShape[1] + j] = sum;
            }
        }

        return new Tensor(resultData, newShape);
    }

    public Tensor elementWiseMultiply(Tensor other) {
        checkCompatibleShape(other);

        float[] resultData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            resultData[i] = data[i] * other.data[i];
        }

        return new Tensor(resultData, shape);
    }

    private void checkMatrixMultiplicationCompatibility(Tensor other) {
        if (shape.length != 2 || other.shape.length != 2 || shape[1] != other.shape[0]) {
            throw new IllegalArgumentException("Invalid shape for matrix multiplication");
        }
    }


    public float get(int... indices) {
        int index = calculateIndex(indices);
        return data[index];
    }

    public void set(float value, int... indices) {
        int index = calculateIndex(indices);
        data[index] = value;
    }

    public Tensor add(Tensor other) {
        checkCompatibleShape(other);
        float[] resultData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            resultData[i] = data[i] + other.data[i];
        }
        return new Tensor(resultData, shape);
    }

    public Tensor multiply(float scalar) {
        float[] resultData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            resultData[i] = data[i] * scalar;
        }
        return new Tensor(resultData, shape);
    }

    private int calculateIndex(int... indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Invalid number of indices");
        }
        int index = 0;
        int stride = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            index += indices[i] * stride;
            stride *= shape[i];
        }
        return index;
    }

    private void checkCompatibleShape(Tensor other) {
        if (!java.util.Arrays.equals(shape, other.shape)) {
            throw new IllegalArgumentException("Tensors must have the same shape");
        }
    }

    private static int calculateSize(int[] shape) {
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        return size;
    }
    public Tensor transpose() {
        if (shape.length != 2) {
            throw new IllegalArgumentException("Transposition is supported only for 2-dimensional tensors");
        }

        int[] newShape = {shape[1], shape[0]};
        float[] transposedData = new float[data.length];

        for (int i = 0; i < shape[0]; i++) {
            for (int j = 0; j < shape[1]; j++) {
                transposedData[j * shape[0] + i] = data[i * shape[1] + j];
            }
        }

        return new Tensor(transposedData, newShape);
    }

    public static Tensor random(int... shape) {
        Random random = new Random();
        float[] data = new float[calculateSize(shape)];
        for (int i = 0; i < data.length; i++) {
            data[i] = random.nextFloat();
        }
        return new Tensor(data, shape);
    }
    public static Tensor dot(Tensor a, Tensor b) {
        if (a.shape.length != 2 || b.shape.length != 2 || a.shape[1] != b.shape[0]) {
            throw new IllegalArgumentException("Invalid shape for matrix multiplication");
        }

        int[] newShape = {a.shape[0], b.shape[1]};
        float[] resultData = new float[newShape[0] * newShape[1]];

        for (int i = 0; i < a.shape[0]; i++) {
            for (int j = 0; j < b.shape[1]; j++) {
                float sum = 0.0f;
                for (int k = 0; k < a.shape[1]; k++) {
                    sum += a.data[i * a.shape[1] + k] * b.data[k * b.shape[1] + j];
                }
                resultData[i * newShape[1] + j] = sum;
            }
        }

        return new Tensor(resultData, newShape);
    }

    public static Tensor elementWiseAdd(Tensor a, Tensor b) {
        a.checkCompatibleShape(b);

        float[] resultData = new float[a.data.length];
        for (int i = 0; i < a.data.length; i++) {
            resultData[i] = a.data[i] + b.data[i];
        }

        return new Tensor(resultData, a.shape);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Tensor(");
        for (int i = 0; i < data.length; i++) {
            builder.append(data[i]);
            if (i < data.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(")");
        return builder.toString();
    }

    public static void main(String[] args) {
        float[] data1 = {1.0f, 2.0f, 3.0f};
        float[] data2 = {4.0f, 5.0f, 6.0f};
        int[] shape = {3};

        Tensor tensor1 = new Tensor(data1, shape);
        Tensor tensor2 = new Tensor(data2, shape);

        Tensor result = tensor1.add(tensor2);
        System.out.println("Addition Result: " + result);

        Tensor multiplied = result.multiply(2.0f);
        System.out.println("Multiplication Result: " + multiplied);
    }
}


