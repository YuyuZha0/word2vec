package org.acc.word2vec.text;

import com.google.common.base.Strings;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;

/**
 * Created by zhaoyy on 2016/12/19.
 */
public final class ChineseTokenPreProcess implements TokenPreProcess {
    @Override
    public String preProcess(String token) {
        token = token.trim();
        if (Strings.isNullOrEmpty(token))
            return null;
        return token;
    }
}
