package flyingkite.algorithm.sequence;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeTraversal {
    // Pre  order = V, L, R
    // In   order = L, V, R
    // Post order = L, R, V

    // -- Iterative solution
    public List<Integer> preorder(TreeNode root) {
        // V L R
        List<Integer> ans = new ArrayList<>();
        ArrayDeque<TreeNode> path = new ArrayDeque<>();
        TreeNode p = root;
        while (!path.isEmpty() || p != null) {
            if (p != null) {
                ans.add(p.val);
                path.push(p);
                p = p.left;
            } else {
                TreeNode end = path.pop();
                p = end.right;
            }
        }
        return ans;
    }

    public List<Integer> preorder2(TreeNode root) {
        // V L R
        List<Integer> ans = new ArrayList<>();
        dfsVLR(root, ans);
        return ans;
    }

    private void dfsVLR(TreeNode x, List<Integer> ans) {
        if (x == null) return;
        ans.add(x.val);
        dfsVLR(x.left, ans);
        dfsVLR(x.right, ans);
    }

    //-- Inorder
    public List<Integer> inorder(TreeNode root) {
        // L V R
        List<Integer> ans = new ArrayList<>();
        ArrayDeque<TreeNode> path = new ArrayDeque<>();
        TreeNode p = root;
        while (!path.isEmpty() || p != null) {
            if (p != null) {
                path.push(p);
                p = p.left;
            } else {
                TreeNode end = path.pop();
                ans.add(end.val);
                p = end.right;
            }
        }
        return ans;
    }
    public List<Integer> inorder2(TreeNode root) {
        // L V R
        List<Integer> ans = new ArrayList<>();
        dfsLVR(root, ans);
        return ans;
    }

    private void dfsLVR(TreeNode x, List<Integer> ans) {
        if (x == null) return;
        dfsLVR(x.left, ans);
        ans.add(x.val);
        dfsLVR(x.right, ans);
    }

    public List<Integer> postorder(TreeNode root) {
        // L R V
        LinkedList<Integer> ans = new LinkedList<>();
        ArrayDeque<TreeNode> path = new ArrayDeque<>();
        TreeNode p = root;
        while (!path.isEmpty() || p == null) {
            if (p != null) {
                path.push(p);
                ans.addFirst(p.val);
                p = p.right; // go right since we listing the R part
            } else {
                TreeNode end = path.pop();
                p = end.left;
            }
        }
        return ans;
    }
    public List<Integer> postorder2(TreeNode root) {
        // L V R
        List<Integer> ans = new ArrayList<>();
        dfsLRV(root, ans);
        return ans;
    }

    private void dfsLRV(TreeNode x, List<Integer> ans) {
        if (x == null) return;
        dfsLRV(x.left, ans);
        dfsLRV(x.right, ans);
        ans.add(x.val);
    }


    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
    }
}
