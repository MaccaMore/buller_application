

import javax.swing.*;

/** ╭┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈╮
 *  | AUTHOR      | Ashley S McKinnon / S3886682                                         |
 *  | COURSE      | Programming A / COSC2410                                             |
 *  | SUBMIT DATE | 27-5-24                                                              |
 *  | TITLE       | Assessment 2 - Mt Buller GUI                                         |
 *  | DESCRIPTION | To create an application that allows customers to create travel      |
 *  |               package deals for Mt Buller resort, including accommodation, Lift    |
 *  |               passes, and lessons. Extended with a GUI.                            |
 *  ╰┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈┈╯
**/



public class MtBullerAdmin {
    public static void main(String[] args) {
        ResortGUI frame = new ResortGUI();

        frame.setSize(860,450);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setTitle("Mt Buller Application");

    }
}
