package techForAll.techPoints.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class LongListToJsonConverter : AttributeConverter<List<Long>, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: List<Long>?): String {
        return attribute?.let { objectMapper.writeValueAsString(it) } ?: "[]"
    }

    override fun convertToEntityAttribute(dbData: String?): List<Long>? {
        return dbData?.let { objectMapper.readValue(it) } ?: emptyList()
    }
}