package dev.minuk.opampcommander.domain.models.agent

data class CustomCapabilities(
    val capabilities: List<String>,
) {
    companion object {
        fun empty(): CustomCapabilities = CustomCapabilities(emptyList())
    }
}
