FastJSON
========

Binary encoder/decoder for efficient transmission of JSON values. 
Inspired in the techniques from FastInfoset [X.891](http://www.itu.int/rec/T-REC-X.891-200505-I), with addtional primitive types procedures for efficient encoding.

It provides encoders and decoders, using the [Java API for JSON processing] (http://docs.oracle.com/javaee/7/api/javax/json/package-summary.html).

It is recommended for enviroments like M2M or IoT, where the volumen is important. Either because of scarce bandwidth or for faster processing of millions of clients at servers.
***

### Some features ###
- Binary codification for primitives types for smaller size and faster processing:
  - INT8, INT16, INT32, INT64
  - UINT8, UINT16, UINT32, UINT64
  - FLOAT32, FLOAT64
  - Boolean
  - Null
- Bulk codification of array of fixed-length types:
  - Arrays of INT8, INT16, INT32, INT64
  - Arrays of UINT8, UINT16, UINT32, UINT64
  - Arrays of FLOAT32, FLOAT64
  - Arrays of booleans
  - Arrays of nulls
- Efficient codification of keys and string values using X.891 techniques.
  - Dictionaries for indexing keys and string values
  - Alphabets
  - Algorithms
- Efficient codification of small identifiers (small unsigned integers)

