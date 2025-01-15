package com.checkout.settlement.{{ cookiecutter.scheme_slug }}.infrastructure.reader;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.Assert;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

@Log4j2
@Getter
public class FileTree<T extends FileLineRecord> {

  @Getter
  public static class LineNode<T extends FileLineRecord> {
    final T value;
    @Setter
    LineNode<T> parent;
    final List<LineNode<T>> children = new ArrayList<>();
    LineNode(T value) {
      this.value = value;
    }
    
    public RecordContext<T> getEdgeToRoot() {
      var context = new HashMap<String, List<FileLineRecord>>();
      for (var child : children) {
        var key = child.getValue().getType();
        context.getOrDefault(key, new ArrayList<>()).add(child.getValue());
      }
      var node = this;
      while ((node = node.getParent()) != null) {
        var key = node.getValue().getType();
        var values = context.getOrDefault(key, new ArrayList<>());
        values.add(node.getValue());
        if (values.size() == 1) {
          context.put(key, values);
        }
      }
      children.clear();
      return new RecordContext<>(getValue(), context);
    }
  }

  public record RecordContext<T extends FileLineRecord>(
      T value,
      Map<String, List<FileLineRecord>> context
  ) {}
  
  private LineNode<T> root;
  private LineNode<T> lastNode;
  private final TreeConfiguration treeConfiguration;

  public FileTree(TreeConfiguration treeConfiguration) {
    this.treeConfiguration = treeConfiguration;
  }
  
  private final BiPredicate<LineNode<T>, LineNode<T>> predicate = new BiPredicate<>() {
    @Override
    public boolean test(LineNode<T> child, LineNode<T> parent) {
      Assert.notNull(child, "Child node is null, file reader error");
      Assert.notNull(parent, "Parent node is null, configuration error");
      var isParent = treeConfiguration.getHierarchy()
          .get(parent.getValue().getType()).contains(child.getValue().getType());
      if (isParent) {
        child.setParent(parent);
        parent.getChildren().add(child);
      }
      return isParent;
    }
  };
  
  public List<LineNode<T>> add(T value) {
    if (value == null) {
      var last = lastNode;
      if (lastNode != null) {
        lastNode = lastNode.getParent();
      }
      return Collections.singletonList(last);
    }
    if (root == null) {
      root = new LineNode<>(value);
      lastNode = root;
      return Collections.emptyList();
    }
    var parent = lastNode;
    lastNode = new LineNode<>(value);
    var nodesPassed = new ArrayList<LineNode<T>>();
    while (!predicate.test(lastNode, parent)) {
      nodesPassed.add(parent);
      parent = parent.getParent();
    }
    return nodesPassed;
  }
  
}
