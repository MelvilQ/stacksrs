# StackSRS - A simple Spaced Repetition app for Android
StackSRS is a flashcard app for Android which helps you to build up a strong active vocabulary in your target language. It uses a simple spaced-repetition algorithm, based on the place of a card in the deck, rather than a time-based algorithm (like SuperMemo2). I created StackSRS because I wished to have something *more minimalistic* and *easier to use* than Anki for my own language learning.

[![Google Play Link](https://play.google.com/intl/en_us/badges/images/badge_new.png)](https://play.google.com/store/apps/details?id=de.melvil.stacksrs)

## F.A.Q.

### Which features does StackSRS have?
* You can create, modify and delete decks and flashcards.
* Learning and reviewing using a simple Spaced Repetition algorithm.
* You can browse through the cards of a deck and search cards by term.
* There is a download function to import pre-made decks (at the moment there are just some decks for the Duolingo courses for German speakers, more is to come).
* The answers can be read out by Text-to-Speech (a voice for the target language has to be installed on the device).
* Decks can be shuffeled and card strengths can be resetted.

### Which features are missing in StackSRS?

* The card content is restricted to text. Images and sound files are not supported.
* Automated deck import from CSV is not yet implemented, but planned in the future. 
* No server synchronization (but you can backup and share your decks via email, Dropbox, Google Drive and other services, which is explained [here](#i-want-to-share-a-deck-with-other-users-what-can-i-do)). 
* No other statistics than the number of mastered and remaining cards per deck (in my experience, too many statistics are not helpful at all, but rather distracting).

### What is Spaced Repetition and why is it a good idea?
We always want to use our time as efficiently as possible. Spaced Repetition apps help you to learn the vocabulary of a new foreign language. It is common sense in the online language learning community that this method is way more efficient than learning words from a list on a paper, or all the other traditional pen-and-paper approaches children are taught at school (vocab books etc). There are always words that we learn very fast, because we know a similar word of another language or we have developed a good memo for it. And then there are the words that we always forget, because they are so difficult, contain strange sound patterns, etc. To use your time effectively, you should spend most of your time with these words and not with the easy ones. Spaced Repetition Systems organize this process for you, so you can totally focus on memorizing the words.

### What is the difference between StackSRS and existing SRS systems?
There are already other popular and widely-used SRS apps, for example Anki. But these systems are time-based. This means that each card shows up again after a certain timespan which is determined by an algorithm. These algorithms are scientifically proven to be very effective. However, this kind of system is very inflexible and does not fit well in my "language learning lifestyle" because it kind of imposes daily goals on me. In Anki, the only real way to control the amount of daily work is to vary the amount of cards added each day, which has a lot of long-term effects. If you feel like doing more than the system expects from you, you have to tweak the overly-complicated settings. If you do less, the number of cards to review grows and grows (as well as the queasy conscience that you get seeing this number as soon as you open the app again). I want to have more flexibility of the daily learning time. Especially I do not want to feel committed to a daily goal or something like that. 

### How do you personally use StackSRS for your language learning?
I usually learn vocabulary only when I have dead time (sitting in a train or bus, waiting in a queue, etc.). There are days when I have many occasions to take out my phone and go through some flashcards for five minutes or so, and on other days I have just some minutes before going to bed. Another factor to consider that differs from day to day is my motivation. By the way, in my experience it is not very effective to spend more than, let's say, 20% of your overall language learning time with flashcarding. Don't forget to practice speaking and, often neglected, listening. And you should never feel obliged to perform any language learning activity unless you really want to. Motivation, not discipline is the key to success.

### How does the spaced repetition algorithm of StackSRS work exactly?
Think of the deck as a stack (hence the name *StackSRS*;). You are always tested with the top card of the stack. After showing the answer, you tell the app if you've answered correct or wrong. If you say *wrong*, the card is put back in the stack and will arrive again after three other cards. If you say *correct*, the card is put further back in the stack. Now it gets a little technical. The position is determined by the following formula: 

**position** = 4^(**strength** + 1),

where *strength* is the number of consecutive times that you knew the correct answer. In other words: After the first time you answer correct, the card will arrive again after 16 other cards, the second time after 64 other cards, the third time after 256 cards, and so on. If you answer wrong, the strength is reduced by 2 levels (it's not set to 0).

### What do the numbers next to the decks stand for?
In the deck list next to each deck, there is a blue, a red and a green number. The blue number shows how many cards a deck has, the red number shows how many of these cards are still considered as *unknown* (because they have a strength of 0, 1 or 2), and the green number how many cards are considered as *known* (strength of 3 and above).

### I want to share a deck with other users. What can I do?
You can backup and share a deck by opening the deck, going to the deck browser and choosing "Share Deck" in the action menu. Then a dialogue should open, in which you can pick a method, for example "Send as email attachment" or "Upload into Dropbox". The options that you have depend on your phone and what is installed there. If you want to share a deck that you have created, so that everyone can profit from it, you can send the deck file to [my email](mailto:patpp17@web.de). Please also add a short description in the email. After having a look at it I will publish it in the download section.

