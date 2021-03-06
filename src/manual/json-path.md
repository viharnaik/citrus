## Using JSONPath

JSONPath is the JSON equivalent to XPath in the XML message world. With JSONPath expressions you can query and manipulate entries of a JSON message structure. The JSONPath expressions evaluate against a JSON message where the JSON object structure is represented in a dot notated syntax.

You will see that JSONPath is a very powerful technology when it comes to find object entries in a complex JSON hierarchy structure. Also JSONPath can help to do message manipulations before a message is sent out for instance. Citrus supports JSONPath expressions in various scenarios:

* &lt;message>&lt;element path="[JSONPath-Expression]">&lt;/message>
* &lt;validate>&lt;json-path expression="[JSONPath-Expression]"/>&lt;/validate>
* &lt;extract>&lt;message path="[JSONPath-Expression]">&lt;/extract>
* &lt;ignore path="[JSONPath-Expression]"/>

### Manipulate with JSONPath

First thing we want to do with JSONPath is to manipulate a message content before it is actually sent out. This is very useful when working with message file resources that are reused across multiple test cases. Each test case can manipulate the message content individually with JSONPath before sending. Lets have a look at this simple sample:

```xml
<message type="json">
  <resource file="file:path/to/user.json" />
  <element path="$.user.name" value="Admin" />
  <element path="$.user.admin" value="true" />
  <element path="$..status" value="closed" />
</message>
```

We use a basic message content file that is called **user.json** . The content of the file is following JSON data structure:

```xml
{ user:
  {
    "id": citrus:randomNumber(10)
    "name": "Unknown",
    "admin": "?",
    "projects":
      [{
        "name": "Project1",
        "status": "open"
      },
      {
        "name": "Project2",
        "status": "open"
      },
      {
        "name": "Project3",
        "status": "closed"
      }]
  }
}
```

Citrus loads the file content and used it as message payload. Before the message is sent out the JSONPath expressions have the chance to manipulate the message content. All JSONPath expressions are evaluated and the give values overwrite existing values accordingly. The resulting message looks like follows:

```xml
{ user:
  {
    "id": citrus:randomNumber(10)
    "name": "Admin",
    "admin": "true",
    "projects":
      [{
        "name": "Project1",
        "status": "closed"
      },
      {
        "name": "Project2",
        "status": "closed"
      },
      {
        "name": "Project3",
        "status": "closed"
      }]
  }
}
```

The JSONPath expressions have set the user name to **Admin** . The **admin** boolean property was set to **true** and all project status values were set to **closed** . Now the message is ready to be sent out. In case a JSONPath expression should fail to find a matching element within the message structure the test case will fail.

With this JSONPath mechanism ou are able to manipulate message content before it is sent or received within Citrus. This makes life very easy when using message resource files that are reused across multiple test cases.

### Validate with JSONPath

Lets continue to use JSONPath expressions when validating a receive message in Citrus:

**XML DSL** 

```xml
<message type="json">
  <validate>
    <json-path expression="$.user.name" value="Penny"/>
    <json-path expression="$['user']['name']" value="${userName}"/>
    <json-path expression="$.user.aliases" value="["penny","jenny","nanny"]"/>
    <json-path expression="$.user[?(@.admin)].password" value="@startsWith('$%00')@"/>
    <json-path expression="$.user.address[?(@.type='office')]"
        value="{"city":"Munich","street":"Company Street","type":"office"}"/>
  </validate>
</message>
```

**Java DSL** 

```xml
receive(someEndpoint)
    .messageType(MessageType.JSON)
    .validate("$.user.name", "Penny")
    .validate("$['user']['name']", "${userName}")
    .validate("$.user.aliases", "["penny","jenny","nanny"]")
    .validate("$.user[?(@.admin)].password", "@startsWith('$%00')@")
    .validate("$.user.address[?(@.type='office')]", "{"city":"Munich","street":"Company Street","type":"office"}");
```

The above JSONPath expressions will be evaluated when Citrus validates the received message. The expression result is compared to the expected value where expectations can be static values as well as test variables and validation matcher expressions. In case a JSONPath expression should not be able to find any elements the test case will also fail.

JSON is a pretty simple yet powerful message format. Simplified a JSON message just knows JSONObject, JSONArray and JSONValue items. The handling of JSONObject and JSONValue items in JSONPath expressions is straight forward. We just use a dot notated syntax for walking through the JSONObject hierarchy. The handling of JSONArray items is also not very difficult either. Citrus will try the best to convert JSONArray items to String representation values for comparison.

**Important**
JSONPath expressions will only work on JSON message formats. This is why we have to tell Citrus the correct message format. By default Citrus is working with XML message data and therefore the XML validation mechanisms do apply by default. With the message type attribute set to **json** we make sure that Citrus enables JSON specific features on the message validation such as JSONPath support.

Now lets get a bit more complex with validation matchers and JSON object functions. Citrus tries to give you the most comfortable validation capabilities when comparing JSON object values and JSON arrays. One first thing you can use is object functions like **keySet()** or **size()** . These functionality is not covered by JSONPath out of the bow but added by Citrus. Se the following example on how to use it:

**XML DSL** 

```xml
<message type="json">
  <validate>
    <json-path expression="$.user.keySet()" value="[id,name,admin,projects]"/>
    <json-path expression="$.user.aliases.size()" value="3"/>
  </validate>
</message>
```

**Java DSL** 

```xml
receive(someEndpoint)
    .messageType(MessageType.JSON)
    .validate("$.user.keySet()", "[id,name,admin,projects]")
    .validate("$.user.aliases.size()", "3");
```

The object functions do return special JSON object related properties such as the set of **keys** for an object or the size of an JSON array.

Now lets get even more comfortable validation capabilities with matchers. Citrus supports Hamcrest matchers which gives us a very powerful way of validating JSON object elements and arrays. See the following examples that demonstrate how this works:

**XML DSL** 

```xml
<message type="json">
  <validate>
    <json-path expression="$.user.keySet()" value="@assertThat(contains(id,name,admin,projects))@"/>
    <json-path expression="$.user.aliases.size()" value="@assertThat(allOf(greaterThan(0), lessThan(5)))@"/>
  </validate>
</message>
```

**Java DSL** 

```xml
receive(someEndpoint)
    .messageType(MessageType.JSON)
    .validate("$.user.keySet()", contains("id","name","admin","projects"))
    .validate("$.user.aliases.size()", allOf(greaterThan(0), lessThan(5)));
```

When using the XML DSL we have to use the **assertThat** validation matcher syntax for defining the Hamcrest matchers. You can combine matcher implementation as seen in the **allOf(greaterThan(0), lessThan(5))** expression. When using the Java DSL you can just add the matcher as expected result object. Citrus evaluates the matchers and makes sure everything is as expected. This is a very powerful validation mechanism as it combines the Hamcrest matcher capabilities with JSON message validation.

### Extract variables with JSONPath

Citrus is able to save message content to test variables at test runtime. When an incoming message is passing the message validation the user can extract some values of that received message to new test variables for later use in the test. This is especially handsome when having to send back some dynamic values. So lets save some values using JSONPath:

```xml
<message type="json">
  <data>
    { user:
      {
        "name": "Admin",
        "password": "secret",
        "admin": "true",
        "aliases": ["penny","chef","master"]
      }
    }
  </data>
  <extract>
    <message path="$.user.name" variable="userName"/>
    <message path="$.user.aliases" variable="userAliases"/>
    <message path="$.user[?(@.admin)].password" variable="adminPassword"/>
  </extract>
</message>
```

With this example we have extracted three new test variables via JSONPath expression evaluation. The three test variables will be available to all upcoming test actions. The variable values are:

```xml
userName=Admin
userAliases=["penny","chef","master"]
adminPassword=secret
```

As you can see we can also extract complex JSONObject items or JSONArray items. The test variable value is a String representation of the complex object.

### Ignore with JSONPath

The next usage scenario for JSONPath expressions in Citrus is the ignoring of elements during message validation. As you already know Citrus provides powerful validation mechanisms for XML and JSON message format. The framework is able to compare received and expected message contents with powerful validator implementations. Now it this time we want to use a JSONPath expression for ignoring a very specific entry in the JSON object structure.

```xml
<message type="json">
  <data>
  {
      "users":
      [{
        "name": "Jane",
        "token": "?",
        "lastLogin": 0
      },
      {
        "name": "Penny",
        "token": "?",
        "lastLogin": 0
      },
      {
        "name": "Mary",
        "token": "?",
        "lastLogin": 0
      }]
  }
  </data>
  <ignore expression="$.users[*].token" />
  <ignore expression="$..lastLogin" />
</message>
```

This time we add JSONPath expressions as ignore statements. This means that we explicitly leave out the evaluated elements from validation. Obviously this mechanism is a good thing to do when dynamic message data simply is not deterministic such as timestamps and dynamic identifiers. In the example above we explicitly skip the **token** entry and all **lastLogin** values that are obviously timestamp values in milliseconds.

The JSONPath evaluation is very powerful when it comes to select a set of JSON objects and elements. This is how we can ignore several elements with one single JSONPath expression which is very powerful.
