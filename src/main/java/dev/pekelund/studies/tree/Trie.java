package dev.pekelund.studies.tree;

import java.util.*;

/**
 * Trie (Prefix Tree) data structure implementation.
 * 
 * A Trie is a tree-like data structure used for efficient storage and retrieval of strings.
 * Each node represents a character, and paths from root to nodes form words.
 * 
 * Time Complexity:
 * - Insert: O(m) where m is the length of the word
 * - Search: O(m)
 * - StartsWith: O(m)
 * - Delete: O(m)
 * 
 * Space Complexity: O(ALPHABET_SIZE * N * M) where N is number of words, M is average length
 * 
 * Use cases:
 * - Autocomplete/type-ahead features
 * - Spell checkers
 * - IP routing tables
 * - Dictionary implementations
 * - String matching algorithms
 */
public class Trie {
    
    /**
     * Node in the Trie. Each node can have up to 26 children (for lowercase letters).
     * We use a HashMap for flexibility to support any characters.
     */
    private static class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        String word; // Store the complete word at terminal nodes for easy retrieval
        
        TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
            word = null;
        }
    }
    
    private final TrieNode root;
    private int wordCount;
    
    /**
     * Initializes an empty Trie.
     */
    public Trie() {
        root = new TrieNode();
        wordCount = 0;
    }
    
    /**
     * Inserts a word into the Trie.
     * 
     * @param word the word to insert (case-insensitive)
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }
        
        word = word.toLowerCase();
        TrieNode current = root;
        
        // Traverse/create path for each character
        for (char ch : word.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }
        
        // Mark end of word
        if (!current.isEndOfWord) {
            wordCount++;
        }
        current.isEndOfWord = true;
        current.word = word;
    }
    
    /**
     * Searches for a complete word in the Trie.
     * 
     * @param word the word to search for
     * @return true if the word exists, false otherwise
     */
    public boolean search(String word) {
        if (word == null) {
            return false;
        }
        
        word = word.toLowerCase();
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord;
    }
    
    /**
     * Checks if there is any word in the Trie that starts with the given prefix.
     * 
     * @param prefix the prefix to search for
     * @return true if any word starts with the prefix, false otherwise
     */
    public boolean startsWith(String prefix) {
        if (prefix == null) {
            return false;
        }
        
        prefix = prefix.toLowerCase();
        return searchNode(prefix) != null;
    }
    
    /**
     * Helper method to find the node corresponding to the last character of a string.
     * 
     * @param str the string to search for
     * @return the node at the end of the path, or null if path doesn't exist
     */
    private TrieNode searchNode(String str) {
        TrieNode current = root;
        
        for (char ch : str.toCharArray()) {
            TrieNode next = current.children.get(ch);
            if (next == null) {
                return null;
            }
            current = next;
        }
        
        return current;
    }
    
    /**
     * Finds all words in the Trie that start with the given prefix.
     * This is the core functionality for autocomplete features.
     * 
     * @param prefix the prefix to search for
     * @return list of all words with the given prefix
     */
    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        
        if (prefix == null) {
            return results;
        }
        
        prefix = prefix.toLowerCase();
        TrieNode node = searchNode(prefix);
        
        if (node == null) {
            return results;
        }
        
        // DFS to find all words from this node
        collectWords(node, results);
        return results;
    }
    
    /**
     * Collects all words from a given node using DFS.
     * 
     * @param node the starting node
     * @param results list to collect words into
     */
    private void collectWords(TrieNode node, List<String> results) {
        if (node.isEndOfWord) {
            results.add(node.word);
        }
        
        for (TrieNode child : node.children.values()) {
            collectWords(child, results);
        }
    }
    
    /**
     * Deletes a word from the Trie.
     * 
     * @param word the word to delete
     * @return true if the word was deleted, false if it didn't exist
     */
    public boolean delete(String word) {
        if (word == null) {
            return false;
        }
        
        word = word.toLowerCase();
        return deleteHelper(root, word, 0);
    }
    
    /**
     * Helper method for deletion using recursion.
     * 
     * @param current current node in recursion
     * @param word the word to delete
     * @param index current character index
     * @return true if the current node should be deleted
     */
    private boolean deleteHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            // Reached end of word
            if (!current.isEndOfWord) {
                return false; // Word doesn't exist
            }
            
            current.isEndOfWord = false;
            current.word = null;
            wordCount--;
            
            // Delete this node if it has no children
            return current.children.isEmpty();
        }
        
        char ch = word.charAt(index);
        TrieNode node = current.children.get(ch);
        
        if (node == null) {
            return false; // Word doesn't exist
        }
        
        boolean shouldDeleteChild = deleteHelper(node, word, index + 1);
        
        if (shouldDeleteChild) {
            current.children.remove(ch);
            // Delete current node if it has no other children and is not end of word
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        
        return false;
    }
    
    /**
     * Returns the number of words in the Trie.
     */
    public int size() {
        return wordCount;
    }
    
    /**
     * Returns all words in the Trie.
     */
    public List<String> getAllWords() {
        List<String> results = new ArrayList<>();
        collectWords(root, results);
        return results;
    }
    
    /**
     * Demonstrates Trie operations with examples.
     */
    public static void demo() {
        System.out.println("=== Trie (Prefix Tree) Demo ===\n");
        
        Trie trie = new Trie();
        
        // Insert words
        System.out.println("Inserting words: cat, car, card, cart, dog, dodge, door");
        String[] words = {"cat", "car", "card", "cart", "dog", "dodge", "door"};
        for (String word : words) {
            trie.insert(word);
        }
        System.out.println("Total words in Trie: " + trie.size() + "\n");
        
        // Search for exact words
        System.out.println("Search operations:");
        System.out.println("  search('cat'): " + trie.search("cat") + " (exists)");
        System.out.println("  search('ca'): " + trie.search("ca") + " (prefix only, not a word)");
        System.out.println("  search('care'): " + trie.search("care") + " (doesn't exist)");
        System.out.println("  search('dog'): " + trie.search("dog") + " (exists)\n");
        
        // Check prefixes
        System.out.println("Prefix checks:");
        System.out.println("  startsWith('ca'): " + trie.startsWith("ca") + " (cat, car, card, cart)");
        System.out.println("  startsWith('do'): " + trie.startsWith("do") + " (dog, dodge, door)");
        System.out.println("  startsWith('bat'): " + trie.startsWith("bat") + " (no words)\n");
        
        // Autocomplete functionality
        System.out.println("Autocomplete (finding all words with prefix):");
        System.out.println("  autocomplete('car'): " + trie.autocomplete("car"));
        System.out.println("  Explanation: Finds all words starting with 'car'\n");
        
        System.out.println("  autocomplete('do'): " + trie.autocomplete("do"));
        System.out.println("  Explanation: Finds all words starting with 'do'\n");
        
        System.out.println("  autocomplete('c'): " + trie.autocomplete("c"));
        System.out.println("  Explanation: Finds all words starting with 'c'\n");
        
        // Get all words
        System.out.println("All words in Trie: " + trie.getAllWords() + "\n");
        
        // Delete operations
        System.out.println("Delete operations:");
        System.out.println("  Deleting 'car': " + trie.delete("car"));
        System.out.println("  After deletion, search('car'): " + trie.search("car"));
        System.out.println("  But 'card' and 'cart' still exist:");
        System.out.println("    autocomplete('car'): " + trie.autocomplete("car"));
        System.out.println("  Total words: " + trie.size() + "\n");
        
        // Practical example: Dictionary with spell suggestions
        System.out.println("Practical Example - Dictionary:");
        Trie dictionary = new Trie();
        String[] dictionaryWords = {
            "apple", "application", "apply", "apartment",
            "banana", "band", "bandana",
            "cat", "catch", "catcher"
        };
        
        for (String word : dictionaryWords) {
            dictionary.insert(word);
        }
        
        System.out.println("User types 'app'...");
        System.out.println("Autocomplete suggestions: " + dictionary.autocomplete("app"));
        System.out.println("\nUser types 'ban'...");
        System.out.println("Autocomplete suggestions: " + dictionary.autocomplete("ban"));
        System.out.println("\nExplanation: Trie enables fast autocomplete in O(m+n) time");
        System.out.println("where m is prefix length and n is number of matching words\n");
    }
}
