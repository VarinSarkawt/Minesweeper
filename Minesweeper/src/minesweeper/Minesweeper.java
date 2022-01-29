
package minesweeper;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Minesweeper extends JFrame implements ActionListener{

    JFrame frame = new JFrame();               
    JButton reset = new JButton("Reset");       //Reset Button as a side.
    JButton End = new JButton("End");    //Similarly, give up button.  
   JButton autoslve=new JButton("autoslove");
    JPanel ButtonPanel = new JPanel();       
    Container grid = new Container();           
    int[][] cell;                             //integer array to store cell of each cell. Used as a back-end for comparisons.
    JButton[][] button;                        //Buttons array to use as a front end for the game.
    int size,diff;                              
    final int MINE = 10;                        
    int mes=5;
    /**
    @param size determines the size of the board
    */

    public Minesweeper(int size){
     super("Minesweeper");                       
     this.size = size;   
     cell = new int[size][size];
     button = new JButton[size][size];  
     grid.setLayout(new GridLayout(size,size));    
     for(int a = 0; a < button.length; a++){
         for(int b = 0; b < button[0].length; b++){
             button[a][b] = new JButton();            
             button[a][b].addActionListener(this);     
             grid.add(button[a][b]);                  
         }
     }
     
     frame.setSize(900,900);                       
     frame.setLayout(new BorderLayout());           
     frame.add(ButtonPanel,BorderLayout.NORTH);     
     reset.addActionListener(this);                 
     End.addActionListener(this); 
     autoslve.addActionListener(this); 
     
     ButtonPanel.add(reset);                        
     ButtonPanel.add(End);       
     ButtonPanel.add(autoslve);  
       
     frame.add(grid,BorderLayout.CENTER);   
     Mine(size);                         //calling function to start the game by filling mines
     frame.setLocationRelativeTo(null);      
     frame.setDefaultCloseOperation(EXIT_ON_CLOSE);     //frame stuff
     frame.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent a) {
        if(a.getSource() == reset){              //resets grid
            for(int i = 0; i < size; i++){
                for(int j = 0; j < size; j++){
                    button[i][j].setEnabled(true);
                    button[i][j].setText("");
                }
            }
            Mine(30);  //triggers a new game.
        }

        else if(a.getSource() == End){  //user has given up. trigger takeTheL( m!= 1).
                   gameend(0); // anything not = 1
                   System.exit(0);
        }
        
        else if(a.getSource() == autoslve){  //user has given up. trigger takeTheL( m!= 1).
             int step=0;
             for(int x = 0; x < size; x++){
                    for( int y = 0; y < size; y++){
                            switch (cell[x][y]) {
                                case MINE:
                                    break;
                                    
                                case 0:
                                    button[x][y].setText(cell[x][y] +"");
                                    button[x][y].setEnabled(false);
                                    ArrayList<Integer> clear = new ArrayList<>();    
                                    clear.add(x*100+y);
                                    Expand(clear); // To recursively clear all surrounding '0' cells.
                                    step++;
                                    break;
                                    
                                default:
                                    button[x][y].setText(""+cell[x][y]);
                                    button[x][y].setEnabled(false);
                                    step++;
                                    break;
                            }
                            
                    }
                }
             
             
             JOptionPane.showMessageDialog(null,"You won the game", "Congratulations!",JOptionPane.INFORMATION_MESSAGE);
             JOptionPane.showMessageDialog(null,step+" step requier to auto-solve", "Congratulations!",JOptionPane.INFORMATION_MESSAGE);
             
        }
        
        else{ // click was on a cell
                for(int x = 0; x < size; x++){
                    for( int y = 0; y < size; y++){
                        if(a.getSource() == button[x][y]){  
                            switch (cell[x][y]) {
                                case MINE:
                                    button[x][y].setForeground(Color.RED);
                                    button[x][y].setIcon(new ImageIcon("images.jpg")); // add bomb image

                                    if( mes == 5){
                                        JOptionPane.showMessageDialog(null,"You clicked a mine becarefull alret!","alret", JOptionPane.ERROR_MESSAGE);
                                        mes=1;
                                    }
                                    else{
                                        mes=5;
                                        gameend(1);
                                    }
                                   
                                    break;
                                    
                                case 0:
                                    button[x][y].setText(cell[x][y] +"");
                                    button[x][y].setEnabled(false);
                                    ArrayList<Integer> clear = new ArrayList<>();    
                                    clear.add(x*100+y);
                                    Expand(clear); // To recursively clear all surrounding '0' cells.
                                    wingame(); //checks win every move
                                    break;
                                    
                                default:
                                    button[x][y].setText(""+cell[x][y]);
                                    button[x][y].setEnabled(false);
                                    wingame(); // its a number > 0 and not a mine, so just check for win
                                    break;
                            }
                        }    
                    }
                }
        }

    }
    /**
     * Function creates mines at random positions.
     * @param s the size of the board(row or column count)
     */
    public void Mine(int s){
    ArrayList<Integer> list = new ArrayList<>();  //Modifiable array to store pos. of mines.
        for(int x = 0; x < s; x++){
            for(int y = 0; y < s; y++){
                list.add(x*100+y);                       // x & y shall be individually retrieved by dividing by 100 and modulo 100 respectively.
                                                         // refer to lines 284 and 285 for implementation
            }
        }
        cell = new int[s][s];                    //resetting back-end array

        for(int a = 0; a < (int)(s * 1.5); a++){
            int choice = (int)(Math.random() * list.size());
            cell [list.get(choice) / 100] [list.get(choice) % 100] = MINE;      //Using corollary of before-last comment to set mines as well.
            list.remove(choice);                                                // We don't want two mines in the same pos., so remove that pos. from list.
        }
        /*
        Following segment initializes 'neighbor cell' for each cell. That is, the number of 
        mines that are present in the eight surrounding cells. IF the cell isn't a mine.
        Note : It is done in the back-end array as that contains the numbers (MINE or 1 or 2 or 3 or 4 or 5 or 6 or 7 or 8)
        */
        for(int x = 0; x < s; x++){
           for(int y = 0; y < s; y++){
            if(cell[x][y] != MINE){
                int neighbor = 0;
                if( x > 0 && y > 0 && cell[x-1][y-1] == MINE){ //top left
                    neighbor++;
                }
                if( y > 0 && cell[x][y-1] == MINE){ //left
                    neighbor++;
                }
                if( y < size - 1 && cell[x][y+1] == MINE){ //right
                    neighbor++;
                }
                if( x < size - 1 && y > 0 && cell[x+1][y-1] == MINE){ //bottom left
                    neighbor++;
                }
                if( x > 0 && cell[x-1][y] == MINE){ //up
                    neighbor++;
                }
                if( x < size - 1 && cell[x+1][y] == MINE){//down
                    neighbor++;
                }
                if( x > 0 && y < size - 1 &&cell[x-1][y+1] == MINE){ //top right
                    neighbor++;
                }
                if( x < size - 1 && y < size - 1 && cell[x+1][y+1] == MINE){ //bottom right
                    neighbor++;
                }
                cell[x][y] = neighbor;  //setting value
            }
           }
        }
    }

    /**
     * This function, called the domino effect, is an implementation of the idea that,
     * when a cell with no surrounding mines is clicked, there's no point in user clicking
     * all eight surrounding cells. Therefore, all surrounding
       cells' cell will be displayed in corresponding cells. 
       The above is done recursively.
     * @param clear the ArrayList which is passed to the function with positions in array
     *                that are zero, and are subsequently clicked.
     */

    public void Expand(ArrayList<Integer> clear){
        if(clear.isEmpty())
            return;                         //base case
        int i = clear.get(0) / 100;       //getting x pos.
        int j = clear.get(0) % 100;       //getting y pos.
        clear.remove(0);                  //remove that element from array to prevent infinite recursion.    
            if(cell[i][j] == 0){                               //similar to neighbor cell, each surrounding cell is filled   
                if( i > 0 && j > 0 && button[i-1][j-1].isEnabled()){ //top left
                    button[i-1][j-1].setText(cell[i-1][j-1] + "");
                    button[i-1][j-1].setEnabled(false);
                    if(cell[i-1][j-1] == 0){
                        clear.add((i-1)*100 + (j-1));     //to recursively implement, each surrounding cell is the new cell,
                                                          // the surrounding cells of which we shall check and so on.
                    }
                }
                if( j > 0 && button[i][j-1].isEnabled()){ //left
                    button[i][j-1].setText(cell[i][j-1] + "");
                    button[i][j-1].setEnabled(false);
                    if(cell[i][j-1] == 0){
                        clear.add(i*100 + (j-1));
                    }
                }
                if( j < size - 1 && button[i][j+1].isEnabled()){ //right
                    button[i][j+1].setText(cell[i][j+1] + "");
                    button[i][j+1].setEnabled(false);
                    if(cell[i][j+1] == 0){
                        clear.add(i*100 + (j+1));
                    }
                }
                if( i < size - 1 && j > 0 && button[i+1][j-1].isEnabled()){ //bottom left     
                    button[i+1][j-1].setText(cell[i+1][j-1] + "");
                    button[i+1][j-1].setEnabled(false);
                    if(cell[i+1][j-1] == 0){
                        clear.add((i+1)*100 + (j-1));
                    }
                }
                if( i > 0 && button[i-1][j].isEnabled()){ //up
                    button[i-1][j].setText(cell[i-1][j] + "");
                    button[i-1][j].setEnabled(false);
                    if(cell[i-1][j] == 0){
                        clear.add((i-1)*100 + j);
                    }
                }
                if( i < size - 1 && button[i+1][j].isEnabled()){ //down
                    button[i+1][j].setText(cell[i+1][j] + "");
                    button[i+1][j].setEnabled(false);
                    if(cell[i+1][j] == 0){
                        clear.add((i+1)*100 + j);
                    }
                }
                if( i > 0 && j < size - 1 && button[i-1][j+1].isEnabled()){ //top right
                    button[i-1][j+1].setText(cell[i-1][j+1] + "");
                    button[i-1][j+1].setEnabled(false);
                    if(cell[i-1][j+1] == 0){
                        clear.add((i-1)*100 + (j+1));
                    }
                }
                if( i < size - 1 && j < size - 1 && button[i+1][j+1].isEnabled()){ //bottom right
                    button[i+1][j+1].setText(cell[i+1][j+1] + "");
                    button[i+1][j+1].setEnabled(false);
                    if(cell[i+1][j+1] == 0){
                        clear.add((i+1)*100 + (j+1));
                    }
                }
            }
            Expand(clear);      //recursive call with list containing surrounding cells, for further check-and-clear of THEIR surr. cells.
    }


        public void gameend(int m){
        for(int i = 0; i < size; i++){
            for(int j = 0; j < size; j++){
                if(button[i][j].isEnabled()){          // when a button has been clicked, it is disabled.
                    if(cell[i][j] != MINE){
                        button[i][j].setText(""+ cell[i][j]);                    
                    }
                    else{
                        button[i][j].setText("X");
                    }
                    button[i][j].setEnabled(false);
                }
            }
        }
        if(m == 1){
            JOptionPane.showMessageDialog(null,"You clicked a mine!","Game Over", JOptionPane.ERROR_MESSAGE);
        }
        else{
            JOptionPane.showMessageDialog(null, "Game Over","Game Over", JOptionPane.ERROR_MESSAGE);
        }
    }
  
    public void wingame() {
       boolean win = true;
       for(int i = 0; i < size; i++){
           for(int j = 0; j < size; j++){
               if(cell[i][j] != MINE && button[i][j].isEnabled()){
                   win = false;
               }
           }
       }
       if(win==true) {
            JOptionPane.showMessageDialog(null,"You won the game", "Congratulations!",JOptionPane.INFORMATION_MESSAGE);
       }   
    }

    public static void main(String[] args){
        new Minesweeper(20);    // Can be made of any size. (For now only squares)

    }

}
