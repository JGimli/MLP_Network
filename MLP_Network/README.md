# MLP Network

MLP Network is a Java/Swing application that uses a small multilayer perceptron to classify handwritten samples of the letters **P**, **R**, and **S**.

The application converts a drawing into a 8 x 8 grid (64 input values), trains a 64-12-6-3 neural network, evaluates the drawing, and lets the user add labeled samples to the training or test datasets.

## Features

- Draw P, R, or S with the mouse
- Convert the drawing to 64 normalized pixel values
- Train and test a multilayer perceptron
- Display confidence values for P, R, and S
- Append new labeled examples to `Train.txt` or `Test.txt`
- Clear and redraw the drawing area
- Include small utilities for visualizing data and generating an average P sample

## Project structure

```text
MLP_Network/
|-- bin/                  Compiled .class files
|-- src/
|   |-- Test.java         Main Swing application and training entry point
|   |-- Siec.java         MLP network implementation
|   |-- Warstwa.java      Neural-network layer implementation
|   |-- Neuron.java       Neuron implementation
|   |-- Pixel_values.java Image-to-pixel conversion helper
|   |-- DataVisualizer.java
|   |-- image_generator.java
|   |-- Okno.java
|   |-- Train.txt         Source copy of training data
|   `-- newtork_heat_map.java
|-- Train.txt             Training dataset used at runtime
|-- Test.txt              Test dataset
`-- README.md
```

## Requirements

- Java Development Kit (JDK) 8 or newer
- A desktop environment with Swing/AWT support
- Windows, Linux, or macOS

No third-party Java libraries or build tools are required.

## Build

From the project directory:

```bat
javac -encoding UTF-8 -d bin src\*.java
```

On PowerShell or POSIX shells, the command is:

```powershell
javac -encoding UTF-8 -d bin src/*.java
```

## Run

Run the main application from the project directory, not from `src/`, because `Train.txt` and `Test.txt` are opened with relative paths:

```bat
java -cp bin Test
```

The application window will:

1. Create a network with 64 inputs, hidden layers of 12 and 6 neurons, and 3 output neurons.
2. Train in repeated passes over `Train.txt`.
3. Continue training until the training accuracy exceeds 75%.
4. Evaluate the network against `Test.txt`; the code invokes the test pass with an 80% target but does not block startup on that result.
5. Display the test accuracy and accept mouse drawings for classification.

Training uses a learning rate of `0.9`.

## Other entry points

Compile the project, then run an individual utility from the project directory:

```bat
java -cp bin DataVisualizer
java -cp bin image_generator
java -cp bin Okno
```

- `Test` - Main P/R/S drawing and classification application
- `DataVisualizer` - Reads `Train.txt` and plots the first two values by category
- `image_generator` - Displays the averaged 8 x 8 representation of P samples
- `Okno` - Standalone drawing window with color and brush-size controls

## Dataset format

Each row in `Train.txt` or `Test.txt` contains:

```text
64 comma-separated normalized values,LABEL
```

The 64 values represent an 8 x 8 pixel grid. Values are between `0` and `1`, where larger values represent stronger ink in the corresponding grid cell. The label is one of:

- `P`
- `R`
- `S`

The project currently includes 453 training rows and 151 test rows.

## Network behavior

The network architecture is:

```text
64 inputs -> 12 neurons -> 6 neurons -> 3 outputs
```

The implementation uses:

- Sigmoid activation
- Random initial weights in the range approximately `-0.01` to `0.01`
- Backpropagation
- One-hot target values for P, R, and S
- A prediction threshold of `0.6`

A result below `0.6` for the strongest output is reported as ambiguous.

## Adding samples

In the main window:

1. Draw a character.
2. Select P, R, or S.
3. Click **Dodaj do ciągu uczącego** to append it to `Train.txt`, or **Dodaj do ciągu testowego** to append it to `Test.txt`.
4. Right-click or use **Wyczyść** to clear the drawing area.

**Dodaj do ciągu uczącego** and **Dodaj do ciągu testowego** mean "Add to training sequence" and "Add to test sequence", respectively.
