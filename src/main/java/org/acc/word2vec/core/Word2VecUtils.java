package org.acc.word2vec.core;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import lombok.NonNull;
import org.acc.word2vec.text.AnsjTokenizerFactory;
import org.acc.word2vec.text.ChineseTokenPreProcess;
import org.acc.word2vec.util.RegexUtils;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.CollectionSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public final class Word2VecUtils {

    private static final Logger logger = LoggerFactory.getLogger(Word2VecUtils.class);

    private Word2VecUtils() {

    }

    private static Word2Vec fit(Collection<String> sentences, File file) {

        if (sentences == null || sentences.isEmpty())
            return null;
        SentenceIterator iterator = new CollectionSentenceIterator(sentences);
        TokenizerFactory tokenizerFactory = new AnsjTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new ChineseTokenPreProcess());

        return fit(iterator, tokenizerFactory, file);
    }

    private static Word2Vec fit(SentenceIterator iterator, TokenizerFactory tokenizerFactory, File file) {

        logger.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .minWordFrequency(5)
                .iterations(1)
                .layerSize(100)
                .seed(42)
                .windowSize(5)
                .iterate(iterator)
                .tokenizerFactory(tokenizerFactory)
                .build();

        logger.info("Fitting Word2Vec model....");
        vec.fit();

        if (file != null) {
            logger.info("model will be write to path[{}]", file.getAbsolutePath());
            WordVectorSerializer.writeWord2VecModel(vec, file);
        } else {
            logger.info("model will not be saved");
        }

        return vec;
    }

    public static Word2Vec restore(@NonNull String path) throws FileNotFoundException {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "illegal path");
        Word2Vec vec = WordVectorSerializer.readWord2VecModel(path);
        return vec;
    }

    private static StringBuilder readAllText(Collection<File> files, Charset charset) {
        StringBuilder builder = new StringBuilder();
        for (File file : files) {
            logger.info("reading text from [{}]...", file.getAbsolutePath());
            List<String> lines = null;
            try {
                lines = Files.readLines(file, charset);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
            if (lines == null)
                continue;
            for (String line : lines)
                builder.append(line);
        }
        return builder;
    }

    private static List<String> splitIntoSentences(CharSequence cs, String regex) {
        return RegexUtils.group(cs, regex);
    }

    public static Builder newWord2Vec() {
        return new Builder();
    }

    public static final class Builder {
        private final Set<File> files = new HashSet<File>();
        private Charset charset = Charsets.UTF_8;
        private File file = null;

        public Builder addTextFile(@NonNull File file) {
            Preconditions.checkArgument(file != null && file.isFile(), "invalid file");
            files.add(file);
            return this;
        }

        public Builder addAllTextFile(@NonNull Collection<File> files) {
            Preconditions.checkArgument(files != null && files.size() != 0, "empty files");
            for (File file : files) {
                addTextFile(file);
            }
            return this;
        }

        public Builder charset(@NonNull Charset charset) {
            Preconditions.checkNotNull(charset, "null charset");
            this.charset = charset;
            return this;
        }

        public Builder saveAt(@NonNull String path, boolean delOld) {
            Preconditions.checkArgument(!Strings.isNullOrEmpty(path), "illegal path");
            File file = new File(path);
            if (file.exists()) {
                logger.info("[{}] already exists", file.getAbsolutePath());
                if (delOld) {
                    logger.info("[{}] will be deleted", file.getAbsolutePath());
                    file.delete();
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                } else {
                    file = null;
                }
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            this.file = file;
            return this;
        }

        public Word2Vec build() {
            CharSequence cs = readAllText(files, charset);
            List<String> sentences = splitIntoSentences(cs, "[^，,。.？?！!\\\\s]+");
            return fit(sentences, file);
        }
    }


}
