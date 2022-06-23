package com.anadol.mindpalace.data.question;

import android.util.Log;

import com.anadol.mindpalace.data.group.Group;
import com.anadol.mindpalace.data.group.Word;
import com.anadol.mindpalace.domain.utils.RandomUtil;

import java.util.ArrayList;
import java.util.Random;

import static com.anadol.mindpalace.view.screens.groupdetail.LearnStartBottomSheet.FORWARD;
import static com.anadol.mindpalace.view.screens.groupdetail.LearnStartBottomSheet.INVERSE;

public class QuestionMaker {
    public static final int EASY_ARRAY_LENGTH = 3;
    public static final int MEDIUM_ARRAY_LENGTH = 5;
    public static final int HARD_ARRAY_LENGTH = 7;
    private static final String TAG = QuestionMaker.class.getName();

    /**
     * Если route = FORWARD, то в качестве вопроса выступает [Число, Дата и/или Событие, Текст, Оригинал и/или Перевод]
     * Иначе, т.е. route = INVERSE, вопросом выступает ассоциация.
     **/
    public Question[] makeQuestions(ArrayList<Word> mWords, int typeGroup, int route) {
        mWords = RandomUtil.getRandomArrayList(mWords, mWords.size());

        int size = mWords.size();
        Question[] questions = new Question[size];

        Word word;
        for (int i = 0; i < size; i++) {
            word = mWords.get(i);
            questions[i] = createNewQuestion(
                    word,
                    getWordsForFalseAnswers(mWords, word),
                    typeGroup,
                    route);
            Log.i(TAG, "makeQuestions: " + questions[i]);
        }

        return questions;
    }

    // Возвращает количество неправильных слов в зависимости от количества успешных повторений слова (3/5/7)
    private Word[] getWordsForFalseAnswers(ArrayList<Word> wordArrayList, Word trueAnswer) {
        int length;
        Word.Difficult difficult = trueAnswer.getDifficult();

        switch (difficult) {
            default:
            case EASY:
                length = EASY_ARRAY_LENGTH;
                break;
            case MEDIUM:
                length = MEDIUM_ARRAY_LENGTH;
                break;
            case HARD:
                length = HARD_ARRAY_LENGTH;
                break;
        }

        Word[] wordsArray = new Word[length];

        ArrayList<Integer> randomArrayList = RandomUtil.getRandomInts(
                length,
                wordArrayList.size(),
                new int[]{wordArrayList.indexOf(trueAnswer)});

        int r;
        for (int i = 0; i < wordsArray.length; i++) {
            r = randomArrayList.get(i);
            wordsArray[i] = wordArrayList.get(r);
        }
        Log.i(TAG, "getWordsForFalseAnswers: " + randomArrayList.toString());

        return wordsArray;
    }


    private Question createNewQuestion(Word word, Word[] wordsForFalseAnswers, int typeGroup, int route) {
        Question question;
        if (route == -1) {
            route = RandomUtil.nextInt(2);
        }

        String questionString = buildQuestion(typeGroup, word);
        String answerString = word.getMultiAssociationFormatSpace();
        String[] falseAnswers = buildFalseAnswers(wordsForFalseAnswers, typeGroup, route, word);

        switch (route) {
            default:
            case FORWARD:
                question = new Question(questionString, answerString, falseAnswers, word);
                break;
            case INVERSE:
                question = new Question(answerString, questionString, falseAnswers, word);
                break;
        }

        question.createAllAnswersRandomOrder();

        return question;
    }

    private String[] buildFalseAnswers(Word[] wordsForFalseAnswers, int typeGroup, int route, Word word) {
        boolean isMultiAssociation = word.isMultiAssociation();

        String[] falseAnswers = new String[wordsForFalseAnswers.length];
        String string = word.getMultiAssociationFormatSpace();
        String a;
        for (int i = 0; i < falseAnswers.length; i++) {
            switch (route) {
                default:
                case FORWARD:
                    if (isMultiAssociation) {
                        a = wordsForFalseAnswers[i].getRandomAssociation();
                        falseAnswers[i] = string.replace(word.getRandomAssociation(), a);
                    } else {
                        falseAnswers[i] = wordsForFalseAnswers[i].getAssociation();
                    }
                    break;
                case INVERSE:
                    falseAnswers[i] = buildQuestion(typeGroup, wordsForFalseAnswers[i]);
                    break;
            }
        }

        return falseAnswers;
    }

    private String buildQuestion(int typeGroup, Word word) {
        String q;
        String original = word.getOriginal();
        String translate = word.getTranslate();

        switch (typeGroup) {
            default:
            case Group.TYPE_NUMBERS:
            case Group.TYPE_TEXTS:
                q = original;
                break;
            case Group.TYPE_DATES:
            case Group.TYPE_LINK:
                if (new Random().nextBoolean()) {
                    q = original;
                } else {
                    q = translate;
                }
                break;
        }

        return q;
    }
}


