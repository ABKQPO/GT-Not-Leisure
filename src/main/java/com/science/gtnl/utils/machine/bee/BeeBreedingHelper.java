package com.science.gtnl.utils.machine.bee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeMutation;
import forestry.api.apiculture.IBeeRoot;
import forestry.api.genetics.IAllele;

public final class BeeBreedingHelper {

    private static final Random RANDOM = new Random();

    private static Map<String, IAlleleBeeSpecies> uidSpeciesCache = null;

    private static Map<String, List<MutationData>> mutationsByUidCache = null;

    private BeeBreedingHelper() {}

    public static IBeeRoot getBeeRoot() {
        return BeeManager.beeRoot;
    }

    public static boolean isBee(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        IBeeRoot root = getBeeRoot();
        return root != null && root.isMember(stack);
    }

    public static boolean isPrincess(ItemStack stack) {
        if (!isBee(stack)) return false;
        return getBeeRoot().getType(stack) == EnumBeeType.PRINCESS;
    }

    public static boolean isDrone(ItemStack stack) {
        if (!isBee(stack)) return false;
        return getBeeRoot().getType(stack) == EnumBeeType.DRONE;
    }

    public static String getBeeUID(ItemStack stack) {
        if (!isBee(stack)) return null;
        IBee bee = getBeeRoot().getMember(stack);
        if (bee == null || bee.getGenome() == null) return null;
        IAlleleBeeSpecies species = bee.getGenome()
            .getPrimary();
        return species != null ? species.getUID() : null;
    }

    private static String stripBeePrefix(String name) {
        if (name == null || name.isEmpty()) return name;

        if (name.length() > 9 && name.regionMatches(true, 0, "SpeciesTE", 0, 9)) {
            return name.substring(9);
        }

        if (name.length() > 7 && name.regionMatches(true, 0, "species", 0, 7)) {
            return name.substring(7);
        }
        return name;
    }

    public static String getSpeciesDisplayName(String input) {
        return input == null || input.isEmpty() ? "" : computeSpeciesDisplayName(input);
    }

    private static String computeSpeciesDisplayName(String input) {

        IAlleleBeeSpecies uidSpecies = getSpeciesByUID(input);
        if (uidSpecies != null) {
            String name = uidSpecies.getName();
            if (name != null && !name.isEmpty() && !"name".equalsIgnoreCase(name)) {
                return name;
            }

            String uln = uidSpecies.getUnlocalizedName();
            String display = extractDisplayFromUnlocalizedName(uln);
            if (display != null) return display;
        }

        String display = extractDisplayFromUnlocalizedName(input);
        if (display != null) return display;

        IAlleleBeeSpecies species = findSpeciesByUnlocalizedName(input);
        if (species != null) {
            String name = species.getName();
            if (name != null && !name.isEmpty() && !"name".equalsIgnoreCase(name)) {
                return name;
            }
        }
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    private static String extractDisplayFromUnlocalizedName(String uln) {
        int lastDot = uln.lastIndexOf('.');
        if (lastDot >= 0) {
            String name = uln.substring(lastDot + 1);
            name = stripBeePrefix(name);
            if (!name.isEmpty()) {
                return Character.toUpperCase(name.charAt(0)) + name.substring(1);
            }
        }

        String stripped = stripBeePrefix(uln);
        if (!stripped.equals(uln) && !stripped.isEmpty()) {
            return Character.toUpperCase(stripped.charAt(0)) + stripped.substring(1);
        }
        return null;
    }

    private static IAlleleBeeSpecies findSpeciesByUnlocalizedName(String uln) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;
        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies s = (IAlleleBeeSpecies) template[0];
                    if (uln.equalsIgnoreCase(s.getUnlocalizedName())) return s;
                }
            }
        }
        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies s = (IAlleleBeeSpecies) template[0];
                if (uln.equalsIgnoreCase(s.getUnlocalizedName())) return s;
            }
        }
        return null;
    }

    public static String getCanonicalUID(String rawName) {
        if (rawName == null || rawName.isEmpty()) return rawName;

        IAlleleBeeSpecies species = getSpeciesByUID(rawName);
        if (species != null) return species.getUID();

        species = getSpeciesByName(rawName);
        if (species != null) return species.getUID();

        String display = getSpeciesDisplayName(rawName);
        if (!display.isEmpty() && !display.equals(rawName)) {
            species = getSpeciesByName(display);
            if (species != null) return species.getUID();
        }
        return rawName;
    }

    private static String extractReadableName(String uln) {
        int lastDot = uln.lastIndexOf('.');
        String name = lastDot >= 0 ? uln.substring(lastDot + 1) : uln;
        return stripBeePrefix(name);
    }

    public static IAlleleBeeSpecies getSpeciesByUID(String uid) {
        if (uid == null || uid.isEmpty()) return null;
        ensureCaches();
        return uidSpeciesCache != null ? uidSpeciesCache.get(uid) : null;
    }

    private static String getDisplayNameForSpecies(IAlleleBeeSpecies species) {

        String name = species.getName();
        if (name != null && !name.isEmpty() && !"name".equalsIgnoreCase(name)) {
            return name;
        }

        String uln = species.getUnlocalizedName();
        if (uln == null || uln.isEmpty()) return "";
        int lastDot = uln.lastIndexOf('.');
        if (lastDot >= 0) {
            String extracted = uln.substring(lastDot + 1);
            extracted = stripBeePrefix(extracted);
            if (!extracted.isEmpty()) {
                return Character.toUpperCase(extracted.charAt(0)) + extracted.substring(1);
            }
        }

        String stripped = stripBeePrefix(uln);
        if (!stripped.equals(uln) && !stripped.isEmpty()) {
            return Character.toUpperCase(stripped.charAt(0)) + stripped.substring(1);
        }
        return Character.toUpperCase(uln.charAt(0)) + uln.substring(1);
    }

    public static boolean tryMutation(double chance) {
        return RANDOM.nextDouble() * 100 < chance;
    }

    public static IAlleleBeeSpecies getSpeciesByName(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                    if (matchSpeciesName(species, speciesName)) {
                        return species;
                    }
                }
            }
        }

        for (IAllele allele : root.getDefaultTemplate()) {
            if (allele instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) allele;
                if (matchSpeciesName(species, speciesName)) {
                    return species;
                }
            }
        }

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] template = mutation.getTemplate();
            if (template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                IAlleleBeeSpecies species = (IAlleleBeeSpecies) template[0];
                if (matchSpeciesName(species, speciesName)) {
                    return species;
                }
            }
        }
        return null;
    }

    private static boolean matchSpeciesName(IAlleleBeeSpecies species, String speciesName) {
        if (species == null || speciesName == null) return false;

        if (species.getName() != null && species.getName()
            .equalsIgnoreCase(speciesName)) {
            return true;
        }

        if (species.getUnlocalizedName() != null) {
            if (species.getUnlocalizedName()
                .equalsIgnoreCase(speciesName)) {
                return true;
            }

            String readable = extractReadableName(species.getUnlocalizedName());
            if (readable.equalsIgnoreCase(speciesName)) {
                return true;
            }

            String display = getDisplayNameForSpecies(species);
            if (display.equalsIgnoreCase(speciesName)) {
                return true;
            }
        }
        return false;
    }

    public static List<MutationData> getMutationsForUID(String uid) {
        ensureCaches();
        if (mutationsByUidCache == null) return new ArrayList<>();
        List<MutationData> cached = mutationsByUidCache.get(uid);

        return cached != null ? new ArrayList<>(cached) : new ArrayList<>();
    }

    public static List<MutationData> getAllMutations() {
        ensureCaches();
        List<MutationData> mutations = new ArrayList<>();
        if (mutationsByUidCache != null) mutationsByUidCache.values()
            .forEach(mutations::addAll);
        return mutations;
    }

    public static ItemStack createDrone(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        IAlleleBeeSpecies species = getSpeciesByUID(speciesName);
        if (species == null) species = getSpeciesByName(speciesName);
        if (species == null) return null;

        IAllele[] template = root.getTemplate(species.getUID());
        if (template == null) return null;

        IBeeGenome genome = root.templateAsGenome(template);
        IBee bee = root.getBee(null, genome);
        if (bee == null) return null;
        return root.getMemberStack(bee, EnumBeeType.DRONE.ordinal());
    }

    public static ItemStack createPrincess(String speciesName) {
        IBeeRoot root = getBeeRoot();
        if (root == null) return null;

        IAlleleBeeSpecies species = getSpeciesByUID(speciesName);
        if (species == null) species = getSpeciesByName(speciesName);
        if (species == null) return null;

        IAllele[] template = root.getTemplate(species.getUID());
        if (template == null) return null;

        IBeeGenome genome = root.templateAsGenome(template);
        IBee bee = root.getBee(null, genome);
        if (bee == null) return null;
        return root.getMemberStack(bee, EnumBeeType.PRINCESS.ordinal());
    }

    private static void ensureCaches() {
        if (uidSpeciesCache != null && mutationsByUidCache != null) return;
        IBeeRoot root = getBeeRoot();
        if (root == null) return;

        Map<String, IAlleleBeeSpecies> speciesMap = new HashMap<>();
        Map<String, List<MutationData>> mutationsMap = new HashMap<>();

        Map<String, IAllele[]> templates = root.getGenomeTemplates();
        if (templates != null) {
            for (IAllele[] template : templates.values()) {
                if (template != null && template.length > 0 && template[0] instanceof IAlleleBeeSpecies) {
                    IAlleleBeeSpecies s = (IAlleleBeeSpecies) template[0];
                    speciesMap.putIfAbsent(s.getUID(), s);
                }
            }
        }

        for (IBeeMutation mutation : root.getMutations(false)) {
            IAllele[] resultTemplate = mutation.getTemplate();
            if (resultTemplate.length == 0 || !(resultTemplate[0] instanceof IAlleleBeeSpecies)) continue;
            IAlleleBeeSpecies resultSpecies = (IAlleleBeeSpecies) resultTemplate[0];
            speciesMap.putIfAbsent(resultSpecies.getUID(), resultSpecies);

            IAllele parent1Allele = mutation.getAllele0();
            IAllele parent2Allele = mutation.getAllele1();
            String p1 = parent1Allele != null ? parent1Allele.getUID() : "";
            String p2 = parent2Allele != null ? parent2Allele.getUID() : "";
            mutationsMap.computeIfAbsent(resultSpecies.getUID(), k -> new ArrayList<>())
                .add(new MutationData(p1, p2, resultSpecies.getUID(), mutation.getBaseChance()));
        }

        uidSpeciesCache = speciesMap;
        mutationsByUidCache = mutationsMap;
    }

    public static class MutationData {

        public final String parent1;
        public final String parent2;
        public final String result;
        public final double chance;

        public MutationData(String parent1, String parent2, String result, double chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.result = result;
            this.chance = chance;
        }
    }

    public static class BreedingStep {

        public final String parent1;
        public final String parent2;
        public final String result;
        public final double chance;

        public BreedingStep(String parent1, String parent2, String result, double chance) {
            this.parent1 = parent1;
            this.parent2 = parent2;
            this.result = result;
            this.chance = chance;
        }

        @Override
        public String toString() {
            return parent1 + " + " + parent2 + " -> " + result;
        }
    }

}
