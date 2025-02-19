package dev.macrobug.mcts4j.games.tarot;

import io.github.nejc92.mcts.MctsDomainState;

import java.util.*;

public class State implements MctsDomainState<Card, Player> {
    private final ArrayList<Card> preseNS= new ArrayList<>();
    private final ArrayList<Card> preseEW= new ArrayList<>();
    private final ArrayList<Game> games = new ArrayList<>();
    private final Player[] players;
    public static final List<Card> allCards = Collections.unmodifiableList(initializeDeck());

    public static int getPoints(ArrayList<Card> prese,Game game) {
        // CAPPOTTO
        if(prese.size()==1) prese.remove(Card.MATTO);
        if(prese.size()==61) prese.add(Card.MATTO);
        int trionfi=0;
        int contatori=0;
        int nrSequenze=0;
        int totSequenze=0;
        int nrCriccone=0;
        int totCriccone=0;
        if(prese.contains(Card.MATTO)){
            trionfi++;
            contatori++;
        }
        if(prese.contains(Card.BEGATTO)){
            trionfi++;
            contatori++;
        }
        // SEQUENZE
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
                    else if(tmpContatori>0 && nr>0){
                        done=true;
                        tmpContatori--;
                    }
                    if(done){
                        nrSequenze++;
                        totSequenze+=nr;
                        boolean skip=false;
                        boolean end=false;
                        for(int i=16;i>=5;i--){
                            if(prese.contains(new Card(i,suit))) {
                                totSequenze++;
                                skip=false;
                            }else if(tmpContatori>0 && !skip){
                                totSequenze++;
                                skip=true;
                                tmpContatori--;
                            }else {
                                end=true;
                                break;
                            }
                        }
                        if(!end) {
                            // i moretti fanno caso a sé
                            long nrMoretti = prese.stream().filter((c) -> c.value()==4 && c.suit().equals(Suit.TRIONFI)).count();
                            if(nrMoretti>0){
                                if(nrMoretti==4){
                                    totSequenze+= (int) nrMoretti;
                                    totSequenze+=contatori;
                                } else if(tmpContatori>0){
                                    totCriccone++;
                                }
                            }
                        }
                    }
                }
            }else{
                if(prese.contains(new Card(14,suit))){
                    int nr=0;
                    boolean done=false;
                    for(int i=11;i<14;i++)
                        if(prese.contains(new Card(i,suit)))
                            nr++;
                    if(nr>1) done=true;
                    else if(contatori>0 && nr>0){
                        done=true;
                    }
                    if(done){
                        nrSequenze++;
                        totSequenze+=nr;
                        totSequenze+=contatori;
                        if(prese.contains(new Card(1,suit)))
                            totSequenze++;
                    }
                }
            }
        }
        int nrMoretti = (int) prese.stream().filter((c) -> c.value()==4 && c.suit().equals(Suit.TRIONFI)).count();
        if(nrMoretti>2 || (nrMoretti==2 && contatori>0)){
            nrSequenze++;
            totSequenze+=nrMoretti+contatori-1;
        }
        int nrAssi = (int) prese.stream().filter((c) -> c.value()==1 && !c.suit().equals(Suit.TRIONFI)).count();
        if(nrAssi>2 || (nrAssi==2 && contatori>0)){
            nrSequenze++;
            totSequenze+=nrAssi+contatori-1;
        }

        // CRICCONE
        if(prese.contains(Card.ANGELO)){
            trionfi++;
        }
        if(prese.contains(Card.MONDO)){
            trionfi++;
        }
        if(trionfi>2){
            nrCriccone++;
            totCriccone+=18*(trionfi>3?2:1);
        }
        for(int i=11;i<=14;i++){
            final int f=i;
            long nr = prese.stream().filter((c) -> c.value()==f && !c.suit().equals(Suit.TRIONFI)).count();
            if(nr>2){
                nrCriccone++;
                totCriccone+=switch (i){
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
                (c.suit().equals(Suit.TRIONFI) || c.value()<=10)).count();
        nrUno-=(nrDue+nrTre+nrQuattro+nrCinque);
        nrUno = prese.contains(Card.MATTO) ? Math.floorDiv(nrUno, 2) : Math.ceilDiv(nrUno, 2);
        // ULTIMA PRESA
        // SE L'ULTMA PRESA È DI MORETTI NON FUNZIONA
        ArrayList<Card> last = game.getCards()[0]!=null ? new ArrayList<>(List.of(game.getCards())) : new ArrayList<>();
        ArrayList<Card> pulite = new ArrayList<>(last.stream().filter((c)->!c.equals(Card.MATTO)).toList());
        int ultimaPresa = 0;
        if(!pulite.isEmpty()){
            if(prese.contains(pulite.getFirst()))
                ultimaPresa=6;
        }else{
            Card l = game.getCards()[3];
            if(l!=null && prese.contains(l))
                ultimaPresa=6;
        }
        return totSequenze*(nrSequenze>2?10:5)+totCriccone*(nrCriccone>2?2:1)+ultimaPresa+(nrDue*2)+(nrTre*3)+(nrQuattro*4)+(nrCinque*5)+nrUno;
    }

    public Game getCurrentGame(){
        return games.getLast();
    }
    public ArrayList<Card> getPreseNS(){return preseNS;}
    public ArrayList<Card> getPreseEW(){return preseEW;}

    public static State initialize(int firstPlayer) {
        Player[] players = initializePlayers(firstPlayer);
        State state = new State(new Game((firstPlayer+1)%4), players);
        // azione dello scartatore
        // per ora lo facciamo statico
        if (firstPlayer % 2 == 0)
            state.preseNS.addAll(scartataIniziale(players[firstPlayer]));
        else
            state.preseEW.addAll(scartataIniziale(players[firstPlayer]));
        return state;
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
                    deck.add(new Card(4,suit,i));
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
        List<Card> temp = new ArrayList<>(State.allCards.size());
        temp.addAll(State.allCards);
        Collections.shuffle(temp);
        int delta=1+firstPlayer;
        for(int i=0;i<4;i++){
            ArrayList<Card> x = new ArrayList<>(temp.subList(i * 15, (i < 3 ? 15 : 17) + (i * 15)));
            players[(i+delta)%4] = new Player(x,i);
        }
        return players;
    }

    public static List<Card> scartataIniziale(Player player) {
        ArrayList<Card> carte=player.getDeck();
        record Statistica(boolean capo, int count, Card ultima, Card penultima) implements Comparable<Statistica>{
            @Override
            public int compareTo(Statistica s) {
                if(!this.capo && this.count==1 && !(!s.capo && s.count==1)) return -1;
                if(!this.capo && this.count==2 && (s.capo || s.count>2)) return -1;
                if(!this.capo && s.capo) return -1;
                if(!s.capo && s.count==1 && !(!this.capo && this.count==1)) return 1;
                if(!s.capo && s.count==2 && (this.capo || this.count>2)) return 1;
                if(!s.capo && this.capo) return 1;
                return 1-Integer.compare(this.count,s.count);
            }
        }
        List<Statistica> carteStatistica = new ArrayList<>();
        List<Card> scartate = new ArrayList<>();
        for(Suit suit: Suit.values()){
            int capo=suit.equals(Suit.TRIONFI)?20:14;
            List<Card> cards = carte.stream().filter((c)->c.suit().equals(suit) && !c.equals(Card.MATTO) && !c.equals(Card.BEGATTO)).sorted().toList();
            if(!cards.isEmpty())
                carteStatistica.add(new Statistica(cards.contains(new Card(capo,suit)),cards.size(), cards.get(0),cards.size()>1?cards.get(1):null));
        }
        ArrayList<Statistica> singole=new ArrayList<>(carteStatistica.stream().sorted().toList());
        while(scartate.size()<2){
            Statistica s = singole.removeFirst();
            if(s.ultima!=null && 
                    !s.ultima.equals(Card.MATTO) && 
                    !s.ultima.equals(Card.BEGATTO) && 
                    !s.ultima.equals(Card.MONDO) && 
                    !s.ultima.equals(Card.ANGELO) && (
                        s.ultima.suit().equals(Suit.TRIONFI) ||
                        s.ultima.value()<14
                    )){
                scartate.add(s.ultima);
                player.getDeck().remove(s.ultima);
            }
            if(scartate.size()==2) continue;
            if(s.penultima!=null &&
                    !s.penultima.equals(Card.MATTO) &&
                    !s.penultima.equals(Card.BEGATTO) &&
                    !s.penultima.equals(Card.MONDO) &&
                    !s.penultima.equals(Card.ANGELO) && (
                        s.penultima.suit().equals(Suit.TRIONFI) ||
                        s.penultima.value()<14
            )){
                scartate.add(s.penultima);
                player.getDeck().remove(s.penultima);
            }
        }
        return scartate;
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
        current.nullifyCurrent();
    }

    @Override
    public State skipCurrentAgent() {
        return this;
    }
}
