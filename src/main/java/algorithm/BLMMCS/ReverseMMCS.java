//package algorithm.BLMMCS;
//
//import java.util.ArrayList;
//import java.util.BitSet;
//import java.util.HashSet;
//import java.util.List;
//
///**
// * reverse version of the MMCS algorithm.
// * input S: a hitting set which is possibly non-minimal.
// * the algorithm reverse the traversal of MMCS, remove elements from S and find all minimal hitting set
// */
//public class ReverseMMCS {
//    /**
//     * intermediate hitting set
//     */
//    private BitSet S;
//
//    /**
//     * intermediate hitting sets
//     */
//    private List<BitSet> hitSets;
//
//    /**
//     * crit[i]: subsets for which element i is crucial
//     */
//    private ArrayList<HashSet<Subset>> crit = new ArrayList<>();
//
//    /**
//     * coveredBy[i]: subsets covered by element i
//     */
//    private ArrayList<HashSet<Subset>> coverMap = new ArrayList<>();
//
//    /**
//     * record S walked, by its hashCode
//     */
//    private HashSet<Integer> walked = new HashSet<>();
//
//    public ReverseMMCS(BitSet _S) {
//        S = (BitSet) _S.clone();
//    }
//
//    public ReverseMMCS(BitSet _S, List<Subset> subsets) {
//        S = (BitSet) _S.clone();
//
//        int nAllElements = S.size();
//        crit = new ArrayList<>(nAllElements);
//        coverMap = new ArrayList<>(nAllElements);
//        for (int i = 0; i < nAllElements; i++) {
//            crit.add(new HashSet<>());
//            coverMap.add(new HashSet<>());
//        }
//
//        for (Subset sb : subsets) {
//            if (sb.getCoverCount() == 1)
//                crit.get(sb.getFirstCover()).add(sb);
//            sb.getAllCovers().forEach(i -> coverMap.get(i).add(sb));
//        }
//    }
//
//    public static void main(String[] args) {
//        ReverseMMCS reverseMMCS = new ReverseMMCS(BitSet.valueOf(new long[]{0b11}));
//
//        Subset A = new Subset(BitSet.valueOf(new long[]{0b01}), BitSet.valueOf(new long[]{0b01}));
//        Subset AB = new Subset(BitSet.valueOf(new long[]{0b11}), BitSet.valueOf(new long[]{0b11}));
//
//        reverseMMCS.crit = new ArrayList<>();
//        reverseMMCS.crit.add(new HashSet<>());
//        reverseMMCS.crit.add(new HashSet<>());
//        reverseMMCS.crit.get(0).add(A);
//
//        reverseMMCS.coverMap = new ArrayList<>();
//        reverseMMCS.coverMap.add(new HashSet<>());
//        reverseMMCS.coverMap.add(new HashSet<>());
//        reverseMMCS.coverMap.get(0).add(A);
//        reverseMMCS.coverMap.get(0).add(AB);
//        reverseMMCS.coverMap.get(1).add(AB);
//
//        System.out.println(reverseMMCS.findAllMinHitSets());
//    }
//
//
//    public List<BitSet> findAllMinHitSets() {
//        findMinHitSet(hitSets);
//        return hitSets;
//    }
//
//    private void findMinHitSet(List<BitSet> res) {
//        if (walked.contains(S.hashCode())) return;
//        walked.add(S.hashCode());   // mark curS been walked
//
//        if (isMinimal()) {
//            res.add((BitSet) S.clone());
//            return;
//        }
//
//        S.stream().forEach(e -> {
//            if (crit.get(e).isEmpty()) {
//                updateCritCoverby(e, S, crit, coverMap);
//                findMinHitSet(res);
//                recoverCritS(e, S, crit, coverMap);
//            }
//        });
//    }
//
//    private void updateCritCoverby(int e, BitSet _S, ArrayList<HashSet<Subset>> _crit, ArrayList<HashSet<Subset>> _coveredBy) {
//        _S.clear(e);
//
//        for (Subset subset : _coveredBy.get(e)) {
//            subset.removeCover(e);
//            if (subset.getCoverCount() == 1) {
//                _crit.get(subset.getFirstCover()).add(subset);
//            }
//        }
//        // _coveredBy.get(e).clear();   // needed in recoverCritS, won't affect other recursions
//    }
//
//    private void recoverCritS(int e, BitSet _S, ArrayList<HashSet<Subset>> _crit, ArrayList<HashSet<Subset>> _coveredBy) {
//        _S.set(e);
//
//        for (Subset subset : _coveredBy.get(e)) {
//            subset.addCover(e);
//            if (subset.getCoverCount() == 2) {
//                _crit.get(subset.getFirstCover()).remove(subset);
//            }
//        }
//    }
//
//    private boolean isMinimal() {
//        for (int i = S.nextSetBit(0); i >= 0; i = S.nextSetBit(i + 1)) {
//            if (crit.get(i).isEmpty()) return false;
//            if (i == Integer.MAX_VALUE) break; // or (i+1) would overflow
//        }
//        return true;
//    }
//
//}
//
