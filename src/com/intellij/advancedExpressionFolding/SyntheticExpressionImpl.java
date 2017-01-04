package com.intellij.advancedExpressionFolding;

import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class SyntheticExpressionImpl extends Expression implements SyntheticExpression {
    private final String text;
    private final ArrayList<Expression> children;

    public SyntheticExpressionImpl(TextRange textRange, String text,
                                   ArrayList<Expression> children) {
        super(textRange);
        this.text = text;
        this.children = children;
    }

    @Override
    public String format() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SyntheticExpressionImpl that = (SyntheticExpressionImpl) o;

        return text.equals(that.text);
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean supportsFoldRegions(Document document) {
        return children.size() > 0 && children.stream().anyMatch(e -> e.supportsFoldRegions(document));
    }

    @Override
    public FoldingDescriptor[] buildFoldRegions(@NotNull PsiElement element, @NotNull Document document) {
        ArrayList<FoldingDescriptor> descriptors = new ArrayList<>();
        for (Expression child : children) {
            if (child.supportsFoldRegions(document)) {
                Collections.addAll(descriptors, child.buildFoldRegions(element, document));
            }
        }
        return descriptors.toArray(FoldingDescriptor.EMPTY);
    }
}