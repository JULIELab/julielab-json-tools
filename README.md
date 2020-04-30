# JULIE Lab JSON Tools

This is a small project that uses the JsonPath library from https://github.com/json-path/JsonPath to simplify some
common JSON file access use cases. The use case solved here is described as follows: Given a JSON file that contains
multiple, similarly-shaped objects (like a list of records of customers or products), retrieve the fields of some
records fields for each record. For example, the file

```json
{
  "books": [
    {
      "title": "Unicorns and Rainbows. An Experience Report.",
      "ISBN": 1
    },
    {
      "title": "The Adventures of John Johnson Smith.",
      "ISBN": 2
    }
  ]
}
```

lists some books. To retrieve one list of title and ISBN for each book in this file, the command line for the tool
would be
 
```
java -jar julielab-json-tools.jar '$.books[*]' '$.title' '$.ISBN'
``` 

The first JsonPath is the 'for each' expression. The following JsonPaths are relative to the result of the first.
Thus, this example reads "for each book return its title as ISBN".

The single quotes are necessary to keep the shell from interpreting the dollar sign which is used to represent the
root of the JSON tree.

The output consists of two lines where the values for different fields (here: title and ISBN) are separated with a
 comma.
```
Unicorns and Rainbows. An Experience Report., 1
The Adventures of John Johnson Smith., 2
```
 
The JsonPath expressions are intepreted and resolved by https://github.com/json-path/JsonPath. There you can learn more
about the exact JsonPath syntax.