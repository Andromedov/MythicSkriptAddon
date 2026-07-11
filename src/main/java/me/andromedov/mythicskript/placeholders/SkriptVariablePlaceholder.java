package me.andromedov.mythicskript.placeholders;

import ch.njol.skript.variables.Variables;

import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.segments.SegmentSource;
import io.lumine.mythic.core.skills.placeholders.segments.types.ResolvedPlaceholderSegment;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Bridges MythicMobs placeholders to Skript's global variable storage.
 * <p>
 * Usage in MythicMobs configs:
 * <pre>
 *   &lt;skript.my_variable&gt; -> value of {my_variable}
 *   &lt;skript.my_list::1&gt; -> value of {my_list::1}
 *   &lt;skript.my_list::*&gt; -> all values of {my_list::*}, comma-separated
 * </pre>
 * <p>
 * Only GLOBAL Skript variables are supported. Local (event-scoped) variables
 * cannot be resolved here because a MythicMobs skill cast has no Bukkit Event
 * tied to a running Skript trigger.
 */
@MythicPlaceholder(
        placeholder = "skript.*",
        usedPlaceholderArguments = -1,
        description = "Returns the value of a global Skript variable (supports list variables via '::')"
)
public class SkriptVariablePlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {

    private final ResolvedPlaceholderSegment<PlaceholderString> variableName;

    public SkriptVariablePlaceholder(GenericPlaceholderArguments metaContext) {
        super(metaContext);
        this.variableName = getPlaceholderString(0, SegmentSource.WILDCARD_ARGS);
        initializeMetaKeywords();
    }

    @Nullable
    @Override
    public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
        String rawName = this.variableName.getOrDefault(placeholderContext, PlaceholderString::get, null);
        if (rawName == null) {
            return null;
        }

        String name = rawName.trim();
        if (name.isEmpty()) {
            return null;
        }

        // "list::*" style request -> flatten the whole list variable into one string
        if (name.endsWith("::*")) {
            return resolveListVariable(name);
        }

        // local=false -> read the GLOBAL {name} variable, no event context needed
        Object value = Variables.getVariable(name, null, false);
        return value == null ? null : String.valueOf(value);
    }

    /**
     * Resolves a Skript list variable (e.g. "my_list::*") into a single,
     * comma-separated string. Returns null if the list is empty or unset.
     */
    @Nullable
    private String resolveListVariable(String listWildcardName) {
        Object raw = Variables.getVariable(listWildcardName, null, false);
        if (!(raw instanceof Map<?, ?> listMap) || listMap.isEmpty()) {
            return null;
        }

        return listMap.values().stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }
}