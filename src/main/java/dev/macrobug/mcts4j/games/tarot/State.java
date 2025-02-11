package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.MctsDomainState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class State implements MctsDomainState<Card, Player> {
    private final ArrayList<Card> preseNS= new ArrayList<>();
    private final ArrayList<Card> preseEW= new ArrayList<>();
    private final ArrayList<Game> games = new ArrayList<>();
    private final Player[] players;
    private static final ArrayList<Card> allCards = initializeDeck();

    // TODO da implementare
    public static int getPoints(ArrayList<Card> prese,Game game) {
        // CAPPOTTO
        if(prese.size()==1) prese.remove(Card.MATTO);
        if(prese.size()==61) prese.add(Card.MATTO);
        int trionfi=0;
        int contatori=0;
        int nrSerie=0;
        int totSerie=0;
        int nrScavezzo=0;
        int totScavezzo=0;
        if(prese.contains(Card.MATTO)){
            trionfi++;
            contatori++;
        }
        if(prese.contains(Card.BEGATTO)){
            trionfi++;
            contatori++;
        }
        // SERIE
        for(Suit suit:Suit.values()){
            if(suit.equals(Suit.TRIONFI)){
                if(prese.contains(Card.ANGELO)){
                    int nr=0;
                    int tmpContatori=contatori;
                    boolean done=false;
                    for(int i=17;i<20;i++)
                        if(prese.contains(new Card(i,suit)))
                            nr++;
                    if(nr>1) done=true;
                    else if(tmpContatori>0){
                        done=true;
                        tmpContatori--;
                    }
                    if(done){
                        nrSerie++;
                        totSerie+=nr;
                        boolean skip=false;
                        boolean end=false;
                        for(int i=16;i>=5;i--){
                            if(prese.contains(new Card(i,suit))) {
                                totSerie++;
                                skip=false;
                            }else if(tmpContatori>0 && !skip){
                                totSerie++;
                                skip=true;
                                tmpContatori--;
                            }else {
                                end=true;
                                break;
                            }
                        }
                        if(!end) {
                            // i moretti fanno caso a sé
                            long nrMoretti = prese.stream().filter((c) -> c.equals(Card.MORETTO)).count();
                            if(nrMoretti>0){
                                if(nrMoretti==4){
                                    totSerie+= (int) nrMoretti;
                                    totSerie+=tmpContatori;
                                } else if(tmpContatori>0){
                                    totScavezzo++;
                                }
                            }
                        }
                    }
                }
            }else{
                if(prese.contains(new Card(14,suit))){
                    int nr=0;
                    int tmpContatori=contatori;
                    boolean done=false;
                    for(int i=11;i<14;i++)
                        if(prese.contains(new Card(i,suit)))
                            nr++;
                    if(nr>1) done=true;
                    else if(tmpContatori>0){
                        done=true;
                        tmpContatori--;
                    }
                    if(done){
                        nrSerie++;
                        totSerie+=nr;
                        totSerie+=tmpContatori;
                        if(prese.contains(new Card(1,suit)))
                            totSerie++;
                    }
                }
            }
        }
        long nrMoretti = prese.stream().filter((c) -> c.equals(Card.MORETTO)).count();
        if(nrMoretti>2 || (nrMoretti==2 && contatori>0)){
            nrSerie++;
            totSerie+=nrMoretti+contatori;
        }
        long nrAssi = prese.stream().filter((c) -> c.value()==1 && !c.suit().equals(Suit.TRIONFI)).count();
        if(nrAssi>2 || (nrAssi==2 && contatori>0)){
            nrSerie++;
            totSerie+=nrAssi+contatori;
        }

        // SCAVEZZO
        if(prese.contains(Card.ANGELO)){
            trionfi++;
        }
        if(prese.contains(Card.MONDO)){
            trionfi++;
        }
        if(trionfi>2){
            nrScavezzo++;
            totScavezzo+=18*(trionfi>3?2:1);
        }
        for(int i=11;i<=14;i++){
            final int f=i;
            long nr = prese.stream().filter((c) -> c.value()==f).count();
            if(nr>2){
                nrScavezzo++;
                totScavezzo+=switch (i){
                    case 11->12;
                    case 12->13;
                    case 13->14;
                    case 14->17;
                    default -> throw new IllegalStateException("Unexpected value: " + i);
                }*(nr>3?2:1);
            }
        }
        // CARTACCE
        int nrCinque = (int) prese.stream().filter((c)->c.equals(Card.BEGATTO) || c.equals(Card.MATTO) || c.equals(Card.MONDO) || c.equals(Card.ANGELO) || (!c.suit().equals(Suit.TRIONFI) && c.value()==14)).count();
        int nrQuattro = (int) prese.stream().filter((c)-> !c.suit().equals(Suit.TRIONFI) && c.value()==13).count();
        int nrTre = (int) prese.stream().filter((c)-> !c.suit().equals(Suit.TRIONFI) && c.value()==12).count();
        int nrDue = (int) prese.stream().filter((c)-> !c.suit().equals(Suit.TRIONFI) && c.value()==11).count();
        int nrUno = (int) prese.stream().filter((c)->
                !c.equals(Card.BEGATTO) &&
                !c.equals(Card.MATTO) &&
                !c.equals(Card.MONDO) &&
                !c.equals(Card.ANGELO) &&
                (!c.suit().equals(Suit.TRIONFI) || c.value()<10)).count();
        nrUno-=nrDue-nrTre-nrQuattro-nrCinque;
        nrUno = prese.contains(Card.MATTO) ? Math.floorDiv(nrUno, 2) : Math.ceilDiv(nrUno, 2);
        // ULTIMA PRESA
        Card last=game.getCards()[3];
        if(last!=null && last.equals(Card.MATTO)) last=game.getCards()[2];
        int ultimaPresa = last!=null && prese.contains(last) ? 6 : 0;
        return totSerie*(nrSerie>2?10:5)+totScavezzo*(nrScavezzo>2?2:1)+ultimaPresa+(nrDue*2)+(nrTre*3)+(nrQuattro*4)+(nrCinque*5)+nrUno;
    }

    public Game getCurrentGame(){
        return games.getLast();
    }
    public ArrayList<Card> getPreseNS(){return preseNS;}
    public ArrayList<Card> getPreseEW(){return preseEW;}

    public static State initialize(int firstPlayer) {
        Player[] players = initializePlayers(firstPlayer);
        return new State(new Game((firstPlayer+1)%4), players);
    }

    private State(Game game, Player[] players) {
        this.players = players;
        games.add(game);
    }

    private static ArrayList<Card> initializeDeck() {
        ArrayList<Card> deck= new ArrayList<>();
        for(Suit suit: Suit.values()){
            if(suit==Suit.TRIONFI){
                deck.add(new Card(0,suit));
                deck.add(new Card(1,suit));
                for(int i=0;i<4;i++)
                    deck.add(new Card(4,suit));
                for(int i=5;i<21;i++)
                    deck.add(new Card(i,suit));
            }else{
                deck.add(new Card(1,suit));
                for(int i=6;i<15;i++)
                    deck.add(new Card(i,suit));
            }
        }
        return deck;
    }

    private static Player[] initializePlayers(int firstPlayer) {
        Player[] players = new Player[4];
        Random rand = new Random(System.currentTimeMillis());
        List<Card> temp = new ArrayList<>(State.allCards.size());
        temp.addAll(State.allCards);
        temp = temp.stream().sorted((_, _)-> Double.compare(rand.nextDouble(), 0.5)).collect(Collectors.toList());
        int delta = 3-firstPlayer;
        for(int i=0;i<4;i++){
            ArrayList<Card> x = new ArrayList<>();
            x.addAll(temp.subList(i*15,(i<3?15:17)+(i*15)));
            players[(i+delta)%4] = new Player(x,i);
        }
        return players;
    }

    @Override
    public boolean isTerminal() {
        return preseEW.size()+preseNS.size()==62;
    }

    private int getCurrentIndex(){
        Game current = games.getLast();
        return current.getTurn();
    }
    @Override
    public Player getCurrentAgent() {
        return players[getCurrentIndex()];
    }

    @Override
    public Player getPreviousAgent() {
        Game current = games.getLast();
        if(!current.isStarted()) {
            current = games.get(games.size() - 2);
            return players[(current.getFirstPlayerIndex()+3)%4];
        }else {
            int currentIndex=getCurrentIndex();
            if(currentIndex==0) currentIndex=4;
            return players[currentIndex- 1];
        }
    }

    @Override
    public int getNumberOfAvailableActionsForCurrentAgent() {
        return getAvailableActionsForCurrentAgent().size();
    }

    // TODO modificarlo per rendere l'informazione incompleta
    @Override
    public List<Card> getAvailableActionsForCurrentAgent() {
        Game currentGame = games.getLast();
        Player currentPlayer = getCurrentAgent();
        List<Card> availableActions = currentPlayer.getDeck();
        if(currentGame.getFirstPlayerIndex()!=getCurrentIndex()){
            if(availableActions.stream().anyMatch((c)->c.suit().equals(currentGame.semeDiMano())))
                availableActions=availableActions.stream().filter((c)->c.suit().equals(currentGame.semeDiMano())).toList();
            else if (availableActions.stream().anyMatch((c)->c.suit().equals(Suit.TRIONFI)))
                availableActions=availableActions.stream().filter((c)->c.suit().equals(Suit.TRIONFI)).toList();
        }
        return availableActions;
    }

    @Override
    public State performActionForCurrentAgent(Card card) {
        validateIsValidAction(card);
        applyActionOnBoard(card);
        return this;
    }
    private void validateIsValidAction(Card action) {
        if (!getAvailableActionsForCurrentAgent().contains(action)) {
            throw new IllegalArgumentException("Error: invalid action passed as function parameter");
        }
    }

    private void applyActionOnBoard(Card card) {
        Game currentGame = games.getLast();
        Player currentPlayer = getCurrentAgent();
        currentPlayer.getDeck().remove(card);
        currentPlayer.getGone().add(card);
        currentGame.setCurrent(card);
        if(currentGame.isDone())
            games.add(generateNextGame(currentGame));
    }
    private record Indici(int matto, int delta,List<Card> presa){}
    private Indici getIndici(Game current){
        boolean trionfo=false;
        Card[] cards = current.getCards();
        int delta=0;
        Card max = cards[0];
        int indiceMatto=-1;
        List<Card> presa=new ArrayList<>();
        for(int i=0;i<4;i++){
            Card card=cards[i];
            if(card.equals(Card.MATTO)) {
                indiceMatto=i;
            }else {
                presa.add(card);
                if (trionfo) {
                    if(card.suit().equals(Suit.TRIONFI) && max.compareTo(card)<0){
                        delta = i;
                        max = card;
                    }
                } else {
                    if (card.suit().equals(Suit.TRIONFI)) {
                        trionfo = true;
                        delta = i;
                        max = card;
                    } else if (card.suit().equals(current.semeDiMano()) && max.compareTo(card)<0) {
                        delta = i;
                        max = card;
                    }
                }
            }
        }
        return new Indici(indiceMatto,delta,presa);
    }
    private Game generateNextGame(Game current){
        Indici indici=getIndici(current);
        if(indici.matto>=0) {
            if ((indici.matto + current.getFirstPlayerIndex()) % 2 == 0)
                preseNS.add(Card.MATTO);
            else
                preseEW.add(Card.MATTO);
        }
        if ((indici.delta + current.getFirstPlayerIndex()) % 2 == 0)
            preseNS.addAll(indici.presa);
        else
            preseEW.addAll(indici.presa);
        return preseEW.size() + preseNS.size() ==62 ? current : new Game((indici.matto + current.getFirstPlayerIndex()) % 4);
    }
    private void destroyGame(Game current){
        Indici indici=getIndici(current);
        if(indici.matto>=0) {
            if ((indici.matto + current.getFirstPlayerIndex()) % 2 == 0)
                preseNS.remove(Card.MATTO);
            else
                preseEW.remove(Card.MATTO);
        }
        if ((indici.delta + current.getFirstPlayerIndex()) % 2 == 0)
            preseNS.removeAll(indici.presa);
        else
            preseEW.removeAll(indici.presa);
    }

    protected State undoAction(Card action) {
        validateIsValidUndoAction(action);
        applyUndoActionOnBoard(action);
        return this;
    }

    private void validateIsValidUndoAction(Card action) {
        Game current=getCurrentGame();
        if(!current.isStarted()){
            current=games.get(games.size()-2);
        }
        Card[] cards=current.getCards();
        for(int i=3;i>=0;i--) {
            Card c=cards[i];
            if (c!=null){
                if(!c.equals(action))
                    throw new IllegalArgumentException("Error: invalid action passed as function parameter");
                break;
            }
        }
    }
    private void applyUndoActionOnBoard(Card card) {
        Game current=getCurrentGame();
        if(!current.isStarted()){
            games.removeLast();
            current=getCurrentGame();
        }
        if(current.isDone()){
            destroyGame(current);
        }
        Player currentPlayer = getCurrentAgent();
        currentPlayer.getDeck().add(card);
        currentPlayer.getGone().remove(card);
        current.setCurrent(null);
    }

    @Override
    public State skipCurrentAgent() {
        return this;
    }
}
