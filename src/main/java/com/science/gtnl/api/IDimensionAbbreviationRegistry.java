package com.science.gtnl.api;

/**
 * Holds the dimension names and abbreviations used by the machines and caches every lookup that succeeded, so resolving
 * a dimension stays a constant time operation once it is known.
 */
public interface IDimensionAbbreviationRegistry {

    /**
     * Registers a resolver that later lookups may consult.
     *
     * @param resolver the resolver to add
     */
    void registerResolver(IDimensionAbbreviationResolver resolver);

    /**
     * Returns the abbreviation of a dimension and binds it on the first lookup that resolves it.
     *
     * @param dimensionId the dimension id to look up
     * @return the abbreviation, or {@code null} when no resolver knows the dimension
     */
    String getAbbreviation(int dimensionId);

    /**
     * Returns the abbreviation of a dimension that is only known by its full name.
     *
     * @param dimensionName the full dimension name
     * @return the abbreviation, or {@code null} when no resolver can abbreviate the name
     */
    String getAbbreviationByName(String dimensionName);

    /**
     * Returns the full name of a dimension.
     *
     * @param dimensionId the dimension id to look up
     * @return the full name, or {@code null} when no resolver knows the dimension or it has no full name
     */
    String getDimensionName(int dimensionId);

    /**
     * Returns the dimension bound to the given abbreviation, which is the reverse of
     * {@link #getAbbreviation(int)}.
     *
     * @param abbreviation the abbreviation to look up
     * @return the dimension id, or {@code null} when the abbreviation is unknown
     */
    Integer getDimensionId(String abbreviation);

    /**
     * Binds an abbreviation to a dimension, replacing every previous binding of either of them.
     *
     * @param dimensionId  the dimension id to bind
     * @param abbreviation the abbreviation to bind, ignored when {@code null}
     */
    void bind(int dimensionId, String abbreviation);
}
