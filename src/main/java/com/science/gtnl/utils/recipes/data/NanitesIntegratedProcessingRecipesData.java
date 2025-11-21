package com.science.gtnl.utils.recipes.data;

public class NanitesIntegratedProcessingRecipesData implements Comparable<NanitesIntegratedProcessingRecipesData> {

    public boolean bioengineeringModule;
    public boolean oreExtractionModule;
    public boolean polymerTwistingModule;

    public NanitesIntegratedProcessingRecipesData(boolean needBioModule, boolean needOreModule,
        boolean needPolyModule) {
        this.bioengineeringModule = needBioModule;
        this.oreExtractionModule = needOreModule;
        this.polymerTwistingModule = needPolyModule;
    }

    @Override
    public int compareTo(NanitesIntegratedProcessingRecipesData o) {
        if (this.bioengineeringModule != o.bioengineeringModule) {
            return Boolean.compare(this.bioengineeringModule, o.bioengineeringModule);
        }

        if (this.oreExtractionModule != o.oreExtractionModule) {
            return Boolean.compare(this.oreExtractionModule, o.oreExtractionModule);
        }

        return Boolean.compare(this.polymerTwistingModule, o.polymerTwistingModule);
    }
}
