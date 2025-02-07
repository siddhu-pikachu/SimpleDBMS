# CobaltDB Implementation Documentation

## Getting Started

### Prerequisites
- Java JDK 17 or higher

### Quick Start
1. Clone the repository:
```bash
git clone <repository-url>
cd my_cobaltdb_project/CobaltDB
```

2. Run the application:
```bash
java -cp target/classes dbms.App
```

### Using the Database
Once running, you'll see the prompt:
```
Welcome to CobaltDB! Type your commands or 'exit' to quit.
Supported commands:
  CREATE TABLE <tablename> (<column>:<type>, ...)
  INSERT INTO <tablename> VALUES (value1, value2, ...)
  .FILE <filename>
  EXIT or QUIT
dbms>
```

Example commands:
```sql
CREATE TABLE users (name:string, age:int);
INSERT INTO users VALUES (John, 25);
.FILE users.csv;
```

To exit: Type `EXIT;` or `QUIT;`

## Technical Implementation

### System Architecture

#### Core Components

1. **App.java (Main Interface)**
   - Provides the command-line interface
   - Handles user input parsing
   - Manages command routing and execution
   - Implements core commands: CREATE TABLE, INSERT INTO, .FILE

2. **Storage Layer**
   - **FileStorage.java**: Manages physical file operations and table management
   - **Page.java**: Implements page-level operations and structure
   - **BPlusTree.java**: Provides B+tree implementation for indexing
   - **Table.java**: Manages table-level operations

3. **Data Management**
   - **Record.java**: Handles record serialization and deserialization
   - **Schema.java**: Manages table schema and metadata
   - **ColumnType.java & DataType.java**: Define and handle data types

### Implementation Details

#### File Structure
- Uses a page-based storage system with 512-byte pages
- Each table is stored in a separate .tbl file
- Pages are structured with:
  - 16-byte header
  - Cell offset array
  - Cell content area

#### B+Tree Implementation
The B+tree implementation provides:
- Efficient record insertion and retrieval
- Automatic page splitting when full
- Maintenance of sorted order by rowid
- Support for both leaf and internal nodes

#### Record Format
Records are stored with:
- 1-byte payload size
- 4-byte rowid
- Variable-length data section
- Deletion marker

#### Page Types
Implements four page types:
- Table Leaf (0x0D)
- Table Interior (0x05)
- Index Leaf (0x0A)
- Index Interior (0x02)

### Header Format and File Structure

#### Page Header Format (16 bytes)
Each page begins with a 16-byte header structured as follows:

| Offset | Size | Description | Example |
|--------|------|-------------|----------|
| 0x00   | 1    | Page Type Flag:<br>- 0x0D: Table Leaf<br>- 0x05: Table Interior<br>- 0x0A: Index Leaf<br>- 0x02: Index Interior | In sample output: `0D` indicates a Table Leaf page |
| 0x01   | 1    | Unused | Always `00` |
| 0x02   | 2    | Number of cells on page | Example: `00 05` means 5 cells |
| 0x04   | 2    | Cell content start offset | Example: `01 01` indicates content starts at offset 257 |
| 0x06   | 4    | Root page number | `00 00 00 01` indicates page 1 is root |
| 0x0A   | 2    | Root page of file | Example: `00 02` indicates page 2 |
| 0x0C   | 2    | Parent page number (0xFFFF if root) | Example: `FF FF` for root page |
| 0x0E   | 2    | Right sibling/child pointer | Example: `00 00` for no right sibling |

#### Reading the Hexdump
Let's analyze a sample from the output:
```hexdump
00000000  0d 00 00 05 01 01 00 00  00 01 00 02 00 00 00 00  |................|
```

Breaking down the first 16 bytes:
- `0d`: Page type (Table Leaf)
- `00`: Unused byte
- `00 05`: 5 cells on this page
- `01 01`: Cell content starts at offset 257
- `00 00 00 01`: Root page number is 1
- `00 02`: Root page of file is 2
- `00 00`: No parent (this is a root)
- `00 00`: No right sibling

#### Record Format in Hexdump
Following the header, each record is stored with:
```hexdump
00000100  00 2d 00 00 00 05 69 00  00 00 00 00 00 00 00 00  |.-....i.........|
          ^^ ^^ ^^^^^^^^^^ ^^^^
          |  |     |        |
          |  |     |        +-- Record data ("i")
          |  |     +-- rowid (5)
          |  +-- Record size (45 bytes)
          +-- Record marker
```

#### Cell Offset Array
The cell offset array follows the header at offset 0x10:
```hexdump
00000010  01 cd 01 9a 01 67 01 34  01 01 00 00 00 00 00 00  |.....g.4........|
```
Each 2-byte value points to the location of a record in the page:
- First record: offset 0x01cd
- Second record: offset 0x019a
- And so on...

#### Sample Data Analysis
From the provided output.tbl hexdump:
```hexdump
00000000  0d 00 00 05 01 01 00 00  00 01 00 02 00 00 00 00  |................|
00000010  01 cd 01 9a 01 67 01 34  01 01 00 00 00 00 00 00  |.....g.4........|
...
00000100  00 2d 00 00 00 05 69 00  00 00 00 00 00 00 00 00  |.-....i.........|
00000110  00 00 00 00 00 00 00 00  00 00 6a 00 00 00 00 00  |..........j.....|
```

This shows:
1. A leaf page (0x0D)
2. Contains 5 records (00 05)
3. First record at offset 0x01cd contains:
   - String 'i' as first column
   - String 'j' as second column
4. Records are stored in reverse order on the page

### Key Features

#### 1. Table Creation
- Supports CREATE TABLE with column definitions
- Handles multiple data types (string, int)
- Automatically generates schema metadata

#### 2. Record Insertion
- Implements INSERT INTO command
- Maintains B+tree structure
- Handles automatic page splits
- Generates monotonically increasing rowids

#### 3. File Management
- Supports .FILE command for CSV processing
- Manages file growth and page allocation
- Maintains data consistency

### Data Types Supported
- INT (4 bytes)
- STRING (variable length)
- Additional types can be easily added through the DataType enum

### Technical Specifications

#### Storage Format
- Page Size: 512 bytes
- Header Size: 16 bytes
- Maximum Records per Page: Variable (depends on record size)
- File Extension: .tbl

#### B+Tree Properties
- Order: 4 (default)
- Split Strategy: Right-heavy
- Key Type: 32-bit integer (rowid)

### Performance Considerations
- Page splits are optimized for sequential inserts
- Record format minimizes internal fragmentation
- B+tree structure ensures O(log n) operations

### Limitations
- No support for DELETE or UPDATE operations
- Limited data type support
- No transaction management
- Single-user system

### Future Enhancements
1. Implementation of DELETE and UPDATE operations
2. Support for additional data types
3. Multi-user concurrency control
4. Transaction management
5. Query optimization

## Conclusion
This implementation provides a functional foundation for a simple database system, demonstrating core concepts of database management including file organization, indexing, and record management. While limited in scope, it successfully implements the core requirements of the DavisBase specification.
```
