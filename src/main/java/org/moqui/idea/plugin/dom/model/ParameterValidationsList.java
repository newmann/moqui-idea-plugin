package org.moqui.idea.plugin.dom.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ParameterValidationsList extends DomElement {

    @NotNull
    @SubTagList(ValOr.TAG_NAME)
    List<ValOr> getValOrList();
    @NotNull
    @SubTagList(ValAnd.TAG_NAME)
    List<ValAnd> getValAndList();

    @NotNull
    @SubTagList(ValNot.TAG_NAME)
    List<ValNot> getValNotList();

    @NotNull
    @SubTagList(Matches.TAG_NAME)
    List<Matches> getMatchesList();

    @NotNull
    @SubTagList(NumberRange.TAG_NAME)
    List<NumberRange> getNumberRangeList();

    @NotNull
    @SubTagList(NumberInteger.TAG_NAME)
    List<NumberInteger> getNumberIntegerList();

    @NotNull
    @SubTagList(NumberDecimal.TAG_NAME)
    List<NumberDecimal> getNumberDecimalList();

    @NotNull
    @SubTagList(TextLength.TAG_NAME)
    List<TextLength> getTextLengthList();

    @NotNull
    @SubTagList(TextEmail.TAG_NAME)
    List<TextEmail> getTextEmailList();
    @NotNull
    @SubTagList(TextUrl.TAG_NAME)
    List<TextUrl> getTextUrlList();
    @NotNull
    @SubTagList(TextLetters.TAG_NAME)
    List<TextLetters> getTextLettersList();
    @NotNull
    @SubTagList(TextDigits.TAG_NAME)
    List<TextDigits> getTextDigitsList();
    @NotNull
    @SubTagList(TimeRange.TAG_NAME)
    List<TimeRange> getTimeRangeList();
    @NotNull
    @SubTagList(CreditCard.TAG_NAME)
    List<CreditCard> getCreditCardList();


}
