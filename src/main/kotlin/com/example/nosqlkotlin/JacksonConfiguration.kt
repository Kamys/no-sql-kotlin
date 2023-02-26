package com.example.nosqlkotlin

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.bson.types.ObjectId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


class ObjectIdSerializer : JsonSerializer<ObjectId>() {
    override fun serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(value.toString())
    }
}

class ObjectIdDeserializer : JsonDeserializer<ObjectId>() {

    override fun deserialize(parser: JsonParser, ctxt: DeserializationContext): ObjectId {
        return ObjectId(parser.valueAsString)
    }
}

@Configuration
class JacksonConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        val module = SimpleModule()
        mapper.registerKotlinModule()
        module.addSerializer(ObjectId::class.java, ObjectIdSerializer())
        module.addDeserializer(ObjectId::class.java, ObjectIdDeserializer())
        mapper.registerModule(module)
        return mapper
    }
}