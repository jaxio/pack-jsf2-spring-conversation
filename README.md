## Celerio Generation Template Pack: JSF 2 Spring Conversation

[![Build Status](https://travis-ci.org/jaxio/pack-jsf2-spring-conversation.svg?branch=master)](https://travis-ci.org/jaxio/pack-jsf2-spring-conversation)

The Celerio Generation Template Pack "JSF 2 Spring Conversation" contains a set of source code file templates that
are interpreted by Celerio code generator in order to generate a web application.

This pack depends on the [JPA Backend pack](https://github.com/jaxio/pack-backend-jpa) template pack.

## Generate an application

This pack is part of Celerio distribution.

Have already Maven 3 and Java 1.8 installed ?

To generate an application from this pack simply execute:

    mvn com.jaxio.celerio:bootstrap-maven-plugin:4.0.9:bootstrap

Please read [Celerio Documentation](http://www.jaxio.com/documentation/celerio) for more details.

## Change Log

### 1.0.4 (2016-09-12)

* Fix inputMultiAutoComplete and inputMultiEnumAutoComplete components
* Fix JS issue in confirmToManyRemoveDialog.xhtml 

### 1.0.3 (2016-08-29)

* Comply with Celerio 4.0.3 which expects celerio-pack.xml to be present (instead of celerio.txt) in the jar of the pack

### 1.0.2

* Uses Celerio 4.0.2. As a result, no longer need to use Jaxio's repository since Celerio is now on Maven Central.

### 1.0.1

* Fix login page i18n rendering
* Clean up generated pom.xml

### 1.0

* initial version, upgraded from our private distribution

## Contributors

In alphabetical order:

* Jean-Louis Boudart
* Florent Ramière
* Nicolas Romanetti

## License

The Celerio Generation Template Pack "JSF 2 Spring Conversation" is released under version 2.0 of
the [Apache License](http://www.apache.org/licenses/LICENSE-2.0).


