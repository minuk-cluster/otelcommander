package dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.converter

import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.config.MongodbReadingConverter
import dev.minuk.opampcommander.adapter.secondary.persistence.mongo.agent.config.MongodbWritingConverter
import dev.minuk.opampcommander.domain.models.agent.AgentCapabilities
import dev.minuk.opampcommander.domain.models.agent.AgentConfigFile
import dev.minuk.opampcommander.domain.models.agent.AgentConfigMap
import dev.minuk.opampcommander.domain.models.agent.AgentDescription
import dev.minuk.opampcommander.domain.models.agent.CommunicationStatus
import dev.minuk.opampcommander.domain.models.agent.ComponentHealth
import dev.minuk.opampcommander.domain.models.agent.CustomCapabilities
import dev.minuk.opampcommander.domain.models.agent.EffectiveConfig
import dev.minuk.opampcommander.domain.models.agent.PackageStatus
import dev.minuk.opampcommander.domain.models.agent.PackageStatuses
import org.bson.Document
import org.bson.types.Binary
import org.springframework.core.convert.converter.Converter
import java.time.Instant
import java.util.Date

enum class SchemaVersion {
    V1,
}

@MongodbReadingConverter
class AgentCapabilitiesReadConverter : Converter<Document, AgentCapabilities> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

    override fun convert(source: Document): AgentCapabilities {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        return AgentCapabilities.of(source.getLong("capabilities"))
    }
}

@MongodbWritingConverter
class AgentCapabilitiesWriteConverter : Converter<AgentCapabilities, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: AgentCapabilities): Document =
        Document()
            .append("schemaVersion", schemaVersion.toString())
            .append("capabilities", source.toLong())
}

@MongodbReadingConverter
class AgentDescriptionReadConverter : Converter<Document, AgentDescription> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

    override fun convert(source: Document): AgentDescription {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        @Suppress("UNCHECKED_CAST")
        return AgentDescription(
            identifyingAttributes = source.get("identifyingAttributes", Map::class.java) as Map<String, String>,
            nonIdentifyingAttributes = source.get("nonIdentifyingAttributes", Map::class.java) as Map<String, String>,
        )
    }
}

@MongodbWritingConverter
class AgentDescriptionWriteConverter : Converter<AgentDescription, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: AgentDescription): Document =
        Document()
            .append("schemaVersion", schemaVersion.toString())
            .append("identifyingAttributes", source.identifyingAttributes as Any)
            .append("nonIdentifyingAttributes", source.nonIdentifyingAttributes as Any)
}

@MongodbReadingConverter
class EffectiveConfigReadConverter : Converter<Document, EffectiveConfig> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)
    val agentConfigMapSubReadConverter = AgentConfigMapSubReadConverter()

    override fun convert(source: Document): EffectiveConfig {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        return EffectiveConfig(
            configMap = agentConfigMapSubReadConverter.convert(source.get("configMap", Document::class.java)),
        )
    }

    class AgentConfigMapSubReadConverter : Converter<Document, AgentConfigMap> {
        val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)
        val agentConfigFileSubReadConverter = AgentConfigFileSubReadConverter()

        override fun convert(source: Document): AgentConfigMap {
            if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
                throw IllegalArgumentException("Unsupported schema version")
            }

            return AgentConfigMap(
                configMap =
                    source.get("configMap", Document::class.java).mapValues {
                        agentConfigFileSubReadConverter.convert(it.value as Document)
                    },
            )
        }

        class AgentConfigFileSubReadConverter : Converter<Document, AgentConfigFile> {
            val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

            override fun convert(source: Document): AgentConfigFile {
                if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
                    throw IllegalArgumentException("Unsupported schema version")
                }

                return AgentConfigFile(
                    body = source.get("body", Binary::class.java).data,
                    contentType = source.getString("contentType"),
                )
            }
        }
    }
}

@MongodbWritingConverter
class EffectiveConfigWriteConverter : Converter<EffectiveConfig, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: EffectiveConfig): Document {
        val agentConfigMapSubWriteConverter = AgentConfigMapSubWriteConverter()

        return Document()
            .append("schemaVersion", schemaVersion.toString())
            .append(
                "configMap",
                agentConfigMapSubWriteConverter.convert(source.configMap),
            )
    }

    class AgentConfigMapSubWriteConverter : Converter<AgentConfigMap, Document> {
        val schemaVersion: SchemaVersion = SchemaVersion.V1
        val agentConfigFileWriteConverter = AgentConfigFileWriteConverter()

        override fun convert(source: AgentConfigMap): Document =
            Document()
                .append("schemaVersion", schemaVersion.toString())
                .append(
                    "configMap",
                    source.configMap.mapValues {
                        agentConfigFileWriteConverter.convert(it.value)
                    },
                )

        class AgentConfigFileWriteConverter : Converter<AgentConfigFile, Document> {
            val schemaVersion: SchemaVersion = SchemaVersion.V1

            override fun convert(source: AgentConfigFile): Document =
                Document()
                    .append("schemaVersion", schemaVersion.toString())
                    .append("body", source.body)
                    .append("contentType", source.contentType)
        }
    }
}

@MongodbReadingConverter
class CommunicationStatusReadConverter : Converter<Document, CommunicationStatus> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

    override fun convert(source: Document): CommunicationStatus {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        return CommunicationStatus(
            sequenceNum = source.getLong("sequenceNum"),
        )
    }
}

@MongodbWritingConverter
class CommunicationStatusWriteConverter : Converter<CommunicationStatus, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: CommunicationStatus): Document =
        Document()
            .append("schemaVersion", schemaVersion.toString())
            .append("sequenceNum", source.sequenceNum)
}

@MongodbReadingConverter
class PackageStatuesReadConverter : Converter<Document, PackageStatuses> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)
    val packageStatusReadConverter = PackageStatusReadConverter()

    override fun convert(source: Document): PackageStatuses {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        @Suppress("UNCHECKED_CAST")
        return PackageStatuses(
            packages =
                (source.get("packages", Map::class.java) as Map<String, Document>).mapValues {
                    packageStatusReadConverter.convert(it.value)
                },
            serverProvidedAllPackagesHash = source.get("serverProvidedAllPackagesHash", Binary::class.java).data,
            errorMessage = source.getString("errorMessage"),
        )
    }

    class PackageStatusReadConverter : Converter<Document, PackageStatus> {
        val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

        override fun convert(source: Document): PackageStatus {
            if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
                throw IllegalArgumentException("Unsupported schema version")
            }

            return PackageStatus(
                name = source.getString("name"),
                agentHasVersion = source.getString("agentHasVersion"),
                agentHasHash = source.getString("agentHasHash"),
                serverOfferedVersion = source.getString("serverOfferedVersion"),
                serverOfferedHash = source.getString("serverOfferedHash"),
                status = PackageStatus.Status.valueOf(source.getString("status")),
                errorMessage = source.getString("errorMessage"),
            )
        }
    }
}

@MongodbWritingConverter
class PackageStatuesWriteConverter : Converter<PackageStatuses, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: PackageStatuses): Document {
        val packageStatusWriteConverter = PackageStatusWriteConverter()

        return Document()
            .append("schemaVersion", schemaVersion.toString())
            .append(
                "packages",
                source.packages.mapValues {
                    packageStatusWriteConverter.convert(it.value)
                },
            ).append("serverProvidedAllPackagesHash", source.serverProvidedAllPackagesHash)
            .append("errorMessage", source.errorMessage)
    }

    class PackageStatusWriteConverter : Converter<PackageStatus, Document> {
        val schemaVersion: SchemaVersion = SchemaVersion.V1

        override fun convert(source: PackageStatus): Document =
            Document()
                .append("schemaVersion", schemaVersion.toString())
                .append("name", source.name)
                .append("agentHasVersion", source.agentHasVersion)
                .append("agentHasHash", source.agentHasHash)
                .append("serverOfferedVersion", source.serverOfferedVersion)
                .append("serverOfferedHash", source.serverOfferedHash)
                .append("status", source.status.name)
                .append("errorMessage", source.errorMessage)
    }
}

@MongodbReadingConverter
class ComponentHealthReadConverter : Converter<Document, ComponentHealth> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

    override fun convert(source: Document): ComponentHealth {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        return ComponentHealth(
            healthy = source.getBoolean("healthy"),
            startedAt = source.getDate("startedAt").toInstant(),
            lastError = source.getString("lastError"),
            status = source.getString("status"),
            statusObservedAt = source.getDate("statusObservedAt").toInstant(),
            componentHealthMap =
                source.get("componentHealthMap", Document::class.java).mapValues {
                    convert(it.value as Document)
                },
        )
    }
}

@MongodbWritingConverter
class ComponentHealthWriteConverter : Converter<ComponentHealth, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: ComponentHealth): Document =
        Document()
            .append("schemaVersion", schemaVersion.toString())
            .append("healthy", source.healthy)
            .append("startedAt", source.startedAt.toDate())
            .append("lastError", source.lastError)
            .append("status", source.status)
            .append("statusObservedAt", source.statusObservedAt.toDate())
            .append(
                "componentHealthMap",
                source.componentHealthMap.mapValues {
                    convert(it.value)
                },
            )

    private fun Instant.toDate(): Date = Date.from(this)
}

@MongodbReadingConverter
class CustomCapabilitiesReadConverter : Converter<Document, CustomCapabilities> {
    val supportSchemaVersion: Set<SchemaVersion> = setOf(SchemaVersion.V1)

    override fun convert(source: Document): CustomCapabilities {
        if (SchemaVersion.valueOf(source.getString("schemaVersion")) !in supportSchemaVersion) {
            throw IllegalArgumentException("Unsupported schema version")
        }

        return CustomCapabilities(
            capabilities = source.getList("capabilities", String::class.java),
        )
    }
}

@MongodbWritingConverter
class CustomCapabilitiesWriteConverter : Converter<CustomCapabilities, Document> {
    val schemaVersion: SchemaVersion = SchemaVersion.V1

    override fun convert(source: CustomCapabilities): Document =
        Document()
            .append("schemaVersion", schemaVersion.toString())
            .append("capabilities", source.capabilities)
}
