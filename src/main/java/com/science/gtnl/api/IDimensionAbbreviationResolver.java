package com.science.gtnl.api;

/**
 * Resolves how a dimension is named and displayed.
 * <p>
 * Resolvers are consulted in registration order until one of them knows the dimension, which keeps cheap sources such
 * as the configuration in front of expensive lookups that have to inspect a world.
 */
public interface IDimensionAbbreviationResolver {

    /**
     * Resolves the names of the given dimension.
     *
     * @param dimensionId the dimension id to resolve
     * @return the names of the dimension, or {@code null} when this resolver does not know the dimension
     */
    DimensionNames resolve(int dimensionId);

    /**
     * Resolves the abbreviation of a dimension that is only known by its full name.
     *
     * @param dimensionName the full dimension name
     * @return the abbreviation, or {@code null} when this resolver cannot abbreviate the name
     */
    default String resolveAbbreviationByName(String dimensionName) {
        return null;
    }
}
