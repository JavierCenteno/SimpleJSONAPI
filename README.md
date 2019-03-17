# SimpleJSONAPI

A simple API for JSON in Java

This repository offers an API and a working implementation. You can view the API in the `api` package and the implementation in the `impl` package.

## How to use

Instance a JSON reader, such as `JsonReaderImplementation`, and use its `read()` method to obtain a JSON structure from the source.

With a JSON value, you can call the method `get(String... keys)` to get any JSON value within it and the method `as(Class<?> resultClass)` to get the JSON value as the given class as long as it's class supported by the implementation.

For example, if you have the JSON value:

```
{
	"employees": [
		{
			"name": "Mary"
		},
		{
			"name": "Paul"
		}
	]
}
```

This would be used to obtain the name of the first employee:

```
String firstEmployeeName = jsonValue.get("employees", "0").as(String.class);
```

