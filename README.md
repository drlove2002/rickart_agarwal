# Ricart-Agrawala Algorithm Visualizer

A Java Swing application that provides real-time visualization of the Ricart-Agrawala distributed mutual exclusion algorithm. This project demonstrates how nodes in a distributed system coordinate to ensure mutual exclusion when accessing critical sections.

## ?? Project Overview

The Ricart-Agrawala algorithm is a distributed algorithm for mutual exclusion in distributed systems. This visualizer brings the algorithm to life by showing:

- **Real-time node communication** with REQUEST and REPLY messages
- **Visual state transitions** as nodes request and enter critical sections  
- **Priority-based conflict resolution** using logical timestamps
- **Interactive network topology** with dynamic node addition/removal

## ? Key Features

### Algorithm Implementation
- **Complete Ricart-Agrawala Protocol**: Full implementation with logical timestamps and priority ordering
- **Thread-Safe Operations**: Concurrent handling of multiple nodes using thread-safe collections
- **Deferred Reply Mechanism**: Proper handling of conflicting requests with priority resolution
- **Logical Clock Synchronization**: Lamport timestamp implementation for ordering events

### Visual Interface
- **Real-time Animation**: Live visualization of message passing between nodes
- **Color-coded States**: 
  - ?? **Green**: IDLE (available for requests)
  - ?? **Orange**: REQUESTING (waiting for critical section access)
  - ?? **Red**: IN_CS (currently in critical section)
- **Message Visualization**:
  - ?? **Blue Arrows**: REQUEST messages
  - ?? **Green Arrows**: REPLY messages
- **Network Statistics**: Real-time display of node counts and state distribution
- **Interactive Legend**: Clear explanation of visual elements

### User Interaction
- **Dynamic Node Management**: Add/remove nodes during runtime (up to 12 nodes)
- **Automatic Positioning**: Smart node placement to avoid overlaps
- **Console Logging**: Detailed algorithm execution logs for analysis

## ??? Architecture

### Core Components

#### `Node.java`
- Implements the core Ricart-Agrawala algorithm logic
- Manages node states, logical clocks, and message handling
- Thread-based execution for concurrent node behavior
- Handles priority resolution for conflicting requests

#### `Graph.java`
- Main visualization panel with custom painting
- Manages visual representation of nodes, edges, and signals
- Provides user interaction through add/remove node functionality
- Displays real-time statistics and legend

#### `SignalManager.java`
- Manages active communication signals between nodes
- Handles REQUEST and REPLY message visualization
- Thread-safe signal storage and cleanup

#### `Edge.java`
- Represents connections between nodes in the network
- Manages edge colors and states during communication
- Provides static methods for edge lifecycle management

#### `ArrowDrawer.java`
- Utility class for drawing directional arrows
- Handles arrow positioning and styling
- Ensures visual clarity with proper spacing and colors

## ?? Getting Started

### Prerequisites
- Java 8 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or command line tools

### Installation & Running

#### Using an IDE:
1. Clone or download the project
2. Open the project in your preferred IDE
3. Navigate to `src/com/app/Main.java`
4. Run the main method

#### Using Command Line:
```bash
# Clone the repository
git clone https://github.com/drlove2002/rickart_agarwal
cd ricart-agrawala-visualizer

# Compile the project
javac -d bin src/com/app/*.java

# Run the application
java -cp bin com.app.Main
```

## ?? How to Use

### Basic Operations
1. **Launch** the application to see the empty network
2. **Add Nodes**: Click "Add Node" to create new nodes (maximum 12)
3. **Remove Nodes**: Click "Remove Node" to remove the most recently added node
4. **Observe**: Watch nodes automatically request and access critical sections

### Understanding the Visualization

#### Node States
- **IDLE (Green)**: Node is available and can make requests
- **REQUESTING (Orange)**: Node is requesting access to critical section
- **IN_CS (Red)**: Node is currently executing in critical section

#### Message Flow
- **Blue Arrows**: REQUEST messages sent when nodes want critical section access
- **Green Arrows**: REPLY messages sent in response to requests
- **Arrow Direction**: Shows the direction of communication between nodes

#### Algorithm Behavior
- Nodes randomly decide to request critical section access
- Conflicting requests are resolved using timestamp priority
- Lower timestamps (or lower node IDs for ties) have higher priority
- Replies are deferred until the critical section is released

### Console Output
The application provides detailed logging in the console:
- Node state changes
- Message sending and receiving
- Critical section entry/exit
- Priority conflict resolution

## ?? Algorithm Details

### Ricart-Agrawala Protocol

The implementation follows the classic Ricart-Agrawala algorithm:

1. **Request Phase**:
   - Node increments its logical clock
   - Sends REQUEST with timestamp to all other nodes
   - Waits for REPLY from all nodes

2. **Reply Decision**:
   - If receiving node is IDLE ? send REPLY immediately
   - If receiving node is in CS ? defer REPLY
   - If receiving node is REQUESTING ? compare timestamps:
     - Lower timestamp has priority ? defer REPLY
     - Higher timestamp ? send REPLY immediately
     - Same timestamp ? lower node ID has priority

3. **Critical Section**:
   - Enter CS when all REPLYs received
   - Execute for random duration (2.5-4 seconds)
   - Exit and send all deferred REPLYs

### Key Implementation Features

- **Thread Safety**: Uses `ConcurrentHashMap` and atomic operations
- **Non-blocking Communication**: Separate threads for CS timing
- **Robust Error Handling**: Graceful handling of node removal during execution
- **Visual Feedback**: Immediate UI updates for all state changes

## ?? Technical Highlights

### Performance Optimizations
- Efficient collision detection for node positioning
- Optimized rendering with high-quality graphics settings
- Minimal object creation during animation loops
- Thread-safe collections for concurrent access

### UI/UX Features
- Modern color scheme with high contrast
- Smooth animations with proper timing
- Responsive layout that adapts to content
- Clear visual hierarchy and information density

### Code Quality
- Comprehensive logging for debugging and analysis
- Clean separation of concerns between components
- Consistent error handling and resource cleanup
- Well-documented public interfaces

## ?? Project Statistics

- **Total Classes**: 8 core classes
- **Lines of Code**: ~1000+ lines
- **Maximum Nodes**: 12 (optimized for visualization)
- **Thread Model**: One thread per node + UI thread
- **Message Types**: REQUEST, REPLY
- **State Management**: Thread-safe concurrent operations

## ?? Known Limitations

- Maximum 12 nodes for optimal visualization performance
- Fixed network topology (fully connected graph)
- No network failure simulation
- Console logging required for detailed analysis

## ?? Future Enhancements

Potential improvements for extended versions:
- **Network Partitioning**: Simulate network failures and partitions
- **Performance Metrics**: Measure algorithm efficiency and fairness
- **Alternative Algorithms**: Compare with other mutual exclusion algorithms
- **Configurable Parameters**: Adjustable timing and probability settings
- **Export Functionality**: Save visualization sequences or logs

## ?? License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## ?? Acknowledgments

- Based on the classic Ricart-Agrawala distributed mutual exclusion algorithm
- Inspired by distributed systems coursework and research
- Built with Java Swing for cross-platform compatibility

---

**Final Project Submission** - This visualizer demonstrates a complete understanding of distributed mutual exclusion algorithms with practical implementation and intuitive visualization.