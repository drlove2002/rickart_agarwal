# Graph Visualizer

A Java Swing application that visualizes dynamic graph networks with animated node communication patterns. This is an MVP demonstrating basic distributed system concepts through visual representation.

## What it does

- Creates an interactive graph where nodes can be added/removed dynamically
- Nodes automatically communicate with each other using request/reply patterns
- Visual feedback through color changes during communication phases
- Real-time animation of network interactions

## Getting Started

### Prerequisites
- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, etc.) or command line

### Running the Application

**Using an IDE:**
1. Clone this repository
2. Open the project in your IDE
3. Run `src/com/app/Main.java`

**Using Command Line:**
```bash
git clone [your-repo-url]
cd rickart_agarwal
javac -d bin src/com/app/*.java
java -cp bin com.app.Main
```

## How to Use

1. **Add Node**: Click the "Add Node" button to create new nodes
2. **Remove Node**: Click "Remove Node" to remove the latest node
3. **Watch**: Observe nodes automatically communicating with color-coded animations
   - Green: Default state
   - Blue: Active communication
   - Colored edges: Communication pathways

## Current Status

This is an MVP (Minimum Viable Product) under active development. Features and implementation details are subject to change.

## Contributing

Feel free to open issues or submit pull requests. Since this is rapidly evolving, please check with maintainers before starting major changes.
