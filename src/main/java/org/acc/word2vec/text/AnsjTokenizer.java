package org.acc.word2vec.text;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public final class AnsjTokenizer implements Tokenizer {

    private static final AtomicIntegerFieldUpdater<AnsjTokenizer> INDEX_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AnsjTokenizer.class, "index");

    private final List<String> tokenizer;
    private volatile int index = 0;
    private TokenPreProcess tokenPreProcess;

    public AnsjTokenizer(String toTokenize) {
        this.tokenizer = tokenize(toTokenize);
    }

    private static List<String> tokenize(String toTokenize) {
        if (Strings.isNullOrEmpty(toTokenize))
            return Collections.emptyList();
        Iterator<Term> iterator = ToAnalysis.parse(toTokenize)
                .iterator();
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        while (iterator.hasNext()) {
            String name = iterator.next().getName();
            if (!Strings.isNullOrEmpty(name))
                builder.add(name);
        }
        return builder.build();
    }

    @Override
    public boolean hasMoreTokens() {
        return index < tokenizer.size();
    }

    @Override
    public int countTokens() {
        return tokenizer.size();
    }

    @Override
    public String nextToken() {
        int i = INDEX_UPDATER.getAndIncrement(this);
        Preconditions.checkPositionIndex(i, tokenizer.size());
        String base = tokenizer.get(i);
        if (tokenPreProcess != null)
            base = tokenPreProcess.preProcess(base);
        return base;
    }

    @Override
    public List<String> getTokens() {
        return tokenizer;
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess tokenPreProcessor) {
        this.tokenPreProcess = tokenPreProcessor;
    }

}
