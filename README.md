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
- Efficient codification of small unsigned integers
- Optionally, arbitrary attachments can be included in a fast json encoding. Very useful to attach data related to the json document.
- Utility class for supporting for org.json package classes (`JSONObject` and `JSONArray`), used in android development.

#### Example of size ####
Given the following JSON document:

```
[
	{
		"id": 0,
		"Vendor":"vendor1",
		"MSISDN":"1234567890",
		"Power":1234567890.123456789,
		"Kc":"BA3DE1F4AABBCCDD",
		"Bitmask":[true,false,true,true,true,true,false,true]
	},
	{
		"id":1,
		"Vendor":"vendor2",
		"MSISDN":"7890123456",
		"Power":9876543210.123456789,
		"Kc":"FFEEDDCCBBAABBCC",
		"Bitmask":[false,false,true,false,true,true,false,true]
	}
]
```
The size of this JSON document is 380 octets in pretty format (whitespace characters). Without whitespace characters is 317 octets and with the Fast JSON encoding is 135 octets. It would fit in a single SMS.

#### Examples of usage ####
You can use builders to get your Json structure (array or object) and then use the FJsonWriter.
This first example shows the building of a Json array without using any primitives or algorithms:
```
JsonArray jsonArray = Json.createArrayBuilder().
                add(Json.createObjectBuilder().
                        add("id", 0).
                        add("Vendor", "vendor1").
                        add("MSISDN","1234567890").
                        add("Power",new BigDecimal("1234567890.123456789")).
                        add("Kc","BA3DE1F4AABBCCDD").
                        add("Bitmask",Json.createArrayBuilder().
                                add(true).add(false).add(true).add(true).add(true).add(true).add(false).add(true))).
                add(Json.createObjectBuilder().
                        add("id", 1).
                        add("Vendor", "vendor2").
                        add("MSISDN","7890123456").
                        add("Power",new BigDecimal("9876543210.123456789")).
                        add("Kc","FFEEDDCCBBAABBCC").
                        add("Bitmask",Json.createArrayBuilder().
                                add(false).add(false).add(true).add(false).add(true).add(true).add(false).add(true))).build();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        FJsonWriter jsonEncoder = new FJsonWriter(baos);
        jsonEncoder.write(jsonArray);
        System.out.println("Size fjson without primitives and algorithms: "+baos.size());
        System.out.println("Size JSON string: "+jsonArray.toString().length());
```
Output:
```
Size fjson without primitives and algorithms: 170
Size JSON string: 317
```

This second example uses primitives and algorithms for smaller size:
```
 	baos.reset();
        jsonEncoder.reset();
	jsonArray = Json.createArrayBuilder().
                add(Json.createObjectBuilder().
                        add("id", 0).
                        add("Vendor", "vendor1").
                        add("MSISDN",new JsonAlphabetConstrainedString("1234567890",Numeric.instance)).
                        add("Power",new BigDecimal("1234567890.123456789")).
                        add("Kc",new JsonAlgorithmEncodingString("BA3DE1F4AABBCCDD",HEXADECIMAL.instance)).
                        add("Bitmask",new JsonArrayBoolean(new boolean[] {true,false,true,true,true,true,false,true})
                                            )).
                add(Json.createObjectBuilder().
                        add("id", 1).
                        add("Vendor", "vendor2").
                        add("MSISDN",new JsonAlphabetConstrainedString("7890123456",Numeric.instance)).
                        add("Power",new BigDecimal("9876543210.123456789")).
                        add("Kc",new JsonAlgorithmEncodingString("FFEEDDCCBBAABBCC",HEXADECIMAL.instance)).
                        add("Bitmask",new JsonArrayBoolean(new boolean[] {false,false,true,false,true,true,false,true})
                                )).build();
       
        jsonEncoder.write(jsonArray);
        System.out.println("Size fjson with primitives and algorithms: "+baos.size());
        System.out.println("Size JSON string: "+jsonArray.toString().length());
```
Output:
```
Size fjson with primitives and algorithms: 135
Size JSON string: 317
```

If you don't want use builders avoiding using Json class, just using the json library interfaces, you can create a `JsonObject` with `JsonObject_impl` and a `JsonArray` with `JsonArray_impl`:
```
        List<JsonValue> list = new LinkedList<JsonValue>();
        Map<String,JsonValue> map = new HashMap<>();
        map.put("id", new JsonNumber_Impl(BigDecimal.ZERO));
        map.put("Vendor", new JsonString_Impl("vendor1"));
        ...
        JsonObject jsonObject = new JsonObject_Impl(map);
        list.add(jsonObject);
        ...
        JsonArray jsonArray = new JsonArray_Impl(list);
```
