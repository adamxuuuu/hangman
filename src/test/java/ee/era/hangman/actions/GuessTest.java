package ee.era.hangman.actions;

import ee.era.hangman.model.Word;
import ee.era.hangman.model.Words;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

public class GuessTest {
  public Locale locale = new Locale("ru");
  Game game = new Game() {
    @Override
    public Locale getLocale() {
      return locale;
    }
  };
  Guess guess = new Guess();

  @Before
  public void startGame() {
    Map<String, Object> session = new HashMap<String, Object>();
    game.setSession(session);
    guess.setSession(session);
    locale = new Locale("en");
    game.words = new Words() {
      @Override
      public Word getRandomWord(String language) {
        if ("ru".equals(language))
          return new Word("методологии разработки", "аджайл");
        if ("es".equals(language))
          return new Word("tarkvara metoodikad", "agiilne");
        return new Word("software development", "agilE");
      }
    };
    game.startGame();
  }

  @Test
  public void ifLetterIsGuessedItsShown() {
    assertEquals("_____", guess.getWordInWork());
    guessLetter('G');
    assertEquals("_g___", guess.getWordInWork());
    assertTrue(guess.isGuessed());
  }

  @Test
  public void initialWordIsCaseInsensitive() {
    assertEquals("_____", guess.getWordInWork());
    guessLetter('e');
    assertEquals("____E", guess.getWordInWork());
    assertTrue(guess.isGuessed());
  }

  @Test
  public void ifLetterIsNotGuessedThenFailuresCounterGrows() {
    guessLetter('x');
    assertEquals("_____", guess.getWordInWork());
    assertFalse(guess.isGuessed());
    assertThat(game.getFailures(), equalTo(1));
  }

  @Test
  public void showsWordWhenGameIsOver() {
    guessLetter('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j');
    assertThat(guess.getFailures(), equalTo(6));
    assertThat(guess.getWordInWork(), equalTo("agilE"));
    assertTrue(guess.isGameOver());
  }

  @Test
  public void supportsRussian() {
    locale = new Locale("ru");
    game.startGame();

    assertEquals("______", guess.getWordInWork());
    guess.letter = 'Ж';
    guess.guessLetter();
    assertEquals("__ж___", guess.getWordInWork());
    assertTrue(guess.isGuessed());
  }

  private void guessLetter(char... letters) {
    for (char letter : letters) {
      guess.letter = letter;
      guess.guessLetter();
    }
  }
}
