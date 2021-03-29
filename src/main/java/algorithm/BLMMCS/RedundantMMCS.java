//package algorithm.BLMMCS;
//
//import java.util.*;
//
//public class RedundantMMCS {
//
//    private int nElements;
//
//    /**
//     * hitting sets that are minimal on its local branch
//     */
//    private List<BitSet> hitSets = new ArrayList<>();
//
//    private LinkedList<Subset> uncov;
//
//    /**
//     * crit[i]: subsets for which element i is crucial
//     */
//    private ArrayList<HashSet<Subset>> crit = new ArrayList<>();
//
//    private BitSet cand;
//
//    public RedundantMMCS(int nEle, List<Subset> subsetsToCover) {
//        nElements = nEle;
//        uncov = new LinkedList<>(subsetsToCover);
//        cand = new BitSet(nElements);
//        cand.set(0, nElements);
//    }
//
//    public void initiate() {
//        findRedundantHitSet(new BitSet(nElements));
//    }
//
//    /**
//     * find all locally minimal hitting sets which are minimal on its local branch
//     * @param S intermediate hitting set
//     */
//    public void findRedundantHitSet(BitSet S) {
//        if (uncov.isEmpty()) {
//            hitSets.add((BitSet) S.clone());
//            return;
//        }
//
//        BitSet C = (BitSet) findBestUncovSubset().elements.clone();
//        C.and(cand);
//
//        C.stream().forEach(e -> {
//            // save context for recover
//            List<Subset> coveredByE = new ArrayList<>();
//            HashMap<Subset, Integer> removedFromCrit = new HashMap<>();
//
//            updateContext(e, S, coveredByE, removedFromCrit);
//            findRedundantHitSet(S);
//            recoverContext(e, S, coveredByE, removedFromCrit);
//        });
//    }
//
//    /**
//     * find an uncovered subset with the biggest intersection with cand
//     */
//    private Subset findBestUncovSubset() {
//        Comparator<Subset> cmp = Comparator.comparing(sb -> {
//            BitSet t = (BitSet) cand.clone();
//            t.and(sb.elements);
//            return t.cardinality();
//        });
//        return Collections.max(uncov, cmp);
//    }
//
//    private void updateContext(int e, BitSet S, List<Subset> coveredByE, HashMap<Subset, Integer> removedFromCrit) {
//        S.set(e);
//
//        uncov.stream().filter(F -> F.hasElement(e)).forEach(F -> {
//            coveredByE.add(F);              // save context for recover
//            F.addCover(e);
//            crit.get(e).add(F);
//            uncov.remove(F);                // TODO: do not remove in stream
//        });
//
//        S.stream().forEach(u -> {
//            crit.get(u).removeIf(F -> {
//                if (!F.hasElement(e)) return false;
//                removedFromCrit.put(F, u);  // save context for recover
//                return true;
//            });
//        });
//    }
//
//    private void recoverContext(int e, BitSet S, List<Subset> coveredByE, HashMap<Subset, Integer> removedFromCrit) {
//        S.clear(e);
//
//        crit.get(e).clear();
//        coveredByE.forEach(F -> {
//            F.removeCover(e);
//            uncov.add(F);
//        });
//
//        removedFromCrit.forEach((subset, critE) -> {
//            crit.get(critE).add(subset);
//        });
//    }
//
//}
