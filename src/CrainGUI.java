/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package clicocranecontrol;


import com.github.sarxos.webcam.Webcam;
import de.re.easymodbus.modbusclient.ModbusClient;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Panel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.List;


/**
 *
 * @author Maciej
 */
public class CrainGUI extends javax.swing.JFrame  {

    
    public static ModbusClient modbus_manual_connection;
    public static Thread t = null;
     boolean thrad_exit = false;
    
    int crane_left = 0;
    int crane_right = 1;
    int crane_down = 3;
    int crane_up = 2;
    int crane_forward = 5;
    int crane_backward = 4;
    int crane_catch = 6;
    
    
    
    // camera
    
    
    Webcam webcam;
    Graphics2D area_wanila;
    Graphics2D area_grey;
    BufferedImage bi_source;
    
    
    
    /**
     * Creates new form CrainGUI
     */
    public CrainGUI() {
        initComponents();
        
      //  getRootPane().setDefaultButton(Button_Manual_Control);
          Button_Manual_Control.requestFocusInWindow();
          modbus_manual_connection = new ModbusClient();
          initialize_thread();
          
          
          
          
          
   //       Dimension dim = new Dimension(640, 480);
            
     //     webcam = Webcam.getDefault();
       //   webcam.setViewSize(dim);
          
         // webcam.open();
          //area_wanila = (Graphics2D) panel_wanila.getGraphics();
    }

    
            public void start_motion()
    {
        
              Thread t1 = new Thread( new Runnable() 
              
              {

                  @Override
                  public void run() 
                  {
                      
                      
                      try
                      {
                      
                          PLC_Debug_Log.append("Video should be present \n"); 
                          BufferedImage bi_gray;
                          
                     while(true)
                     {
                         

                            bi_source = webcam.getImage();
                         //   bi_gray = to_grey_scale(bi_source);
                            area_wanila.drawImage(to_grey_scale(bi_source), null, 0, 0);




                            Thread.sleep(50);
                       
                     }
                     
                      }
                      catch (Exception e)
                      {
                          
                         System.out.println("ERROR " + e.getMessage());
                          
                      }
                      
                     
                  }
              });
              t1.start();
              

              
        
        
    }
    
        private void start_camera()
    {
        
         try 
        {
            
            Dimension dim = new Dimension(640, 480);
            
            //webcam = Webcam.getDefault();
            
            List<Webcam>  available_cams  =   Webcam.getWebcams();
            
            PLC_Debug_Log.append(available_cams.get(0).getName() + "\n");
           // PLC_Debug_Log.append(available_cams.get(1).getName());
            
            webcam = available_cams.get(0);
            
            webcam.setViewSize(dim);
            webcam.open();

            
            
            area_wanila = (Graphics2D) panel_wanila.getGraphics();
      
            
        }
        catch (Exception e)
        {
            
            
            System.out.println("ERROR Start" + e.getMessage());
            
        } 
        
    }
    
        
    private BufferedImage to_grey_scale(BufferedImage bi)
    {
        
      // BufferedImage tmp = bi.getSubimage(0,0,bi.getWidth(), bi.getHeight() );
      // bi = to_grey_scale(webcam.getImage());
        BufferedImage tmp = new BufferedImage(640, 480 , BufferedImage.TYPE_INT_RGB); 
       // int x = 0;
      //  int y = 0;
        
        Color color;
        
        int red;
        int green;
        int blue; 
        int rgb;
        
                
        for (int x = 0 ; x < 640 ; x++)
        {
            for (int y = 0 ; y < 480 ; y++)
            {
             
            
        
                color = new Color(bi.getRGB(x, y));

                red = color.getRed();
                green = color.getGreen();
                blue = color.getBlue();


            //    if (radio_average.isSelected())
                {
                    
                     red = green = blue = (int)   (     ( Math.max(red, Math.max(green, blue))) + Math.min(red, Math.min(green, blue))  )  /2 ;
                 //+ Math.min(red, Math.min(green, blue))) * 0,5     
                }                    
    //            if (radio_light.isSelected())
                {
                    
      //               red = green = blue = (int)((red  + green  + blue ) * 0.3333 );
                
                }                    
        //        if (radio_luminosity.isSelected())
                {
                    
          //           red = green = blue = (int)(red * 0.21 + green * 0.72 + blue * 0.07);
                
                }
                
                
                color = new Color(red, green, blue);

                rgb = color.getRGB();

                
                
                tmp.setRGB(x, y, rgb);
                
                
            }
        }
     
        
        area_wanila.drawImage(tmp, null, 0, 0);
          
        return tmp;
        
    }
         
        
        
        
        
        
        
        
        
        
    
    public void initialize_thread()
    {
        
          t = new Thread()
                    {
                        public void run()
                        {
                            while(!thrad_exit)
                            {
                                
                                try
                                {
                                  
                                     script1();
                                  
                                }
                                catch (Exception e)
                                {
                                  
                                    System.out.print(e.getLocalizedMessage());
                                    
                                }
                            
                            }
                            
                        }
                        
                        
                        
                    };
                    
        
    }
    
    public void script1()
    {
        try
        {
        
        scada_reset_values();
        Thread.sleep(2000);
        

        
        // pozycja startowa
      

        
        scada_set_command(crane_catch, true);
        Thread.sleep(2000);
        
        scada_set_command(crane_up, true);
        Thread.sleep(4000);
        scada_set_command(crane_up, false);
        Thread.sleep(4000); 
        
        
        scada_set_command(crane_left, true);
        Thread.sleep(4000);
        scada_set_command(crane_left, false);
        Thread.sleep(4000); 
        

        scada_set_command(crane_forward, true);
        Thread.sleep(2000);
        scada_set_command(crane_forward, false);
        Thread.sleep(4000);  

        
        scada_set_command(crane_down, true);
        Thread.sleep(3650);
        scada_set_command(crane_down, false);
        Thread.sleep(4000);        

        scada_set_command(crane_catch, false);
        Thread.sleep(2000);  
        
        
        scada_set_command(crane_up, true);
        Thread.sleep(4000);
        scada_set_command(crane_up, false);
        Thread.sleep(4000); 
        
        
        scada_set_command(crane_backward, true);
        Thread.sleep(2000);
        scada_set_command(crane_backward, false);
        Thread.sleep(4000);    
        
        
        scada_set_command(crane_right, true);
        Thread.sleep(4000);
        scada_set_command(crane_right, false);
        Thread.sleep(4000); 
        
        
        scada_set_command(crane_down, true);
        Thread.sleep(3650);
        scada_set_command(crane_down, false);
        Thread.sleep(4000);   
        
        
        
        scada_set_command(crane_up, true);
        Thread.sleep(4000);
        scada_set_command(crane_up, false);
        Thread.sleep(4000); 
        
        
        scada_set_command(crane_left, true);
        Thread.sleep(4000);
        scada_set_command(crane_left, false);
        Thread.sleep(4000); 
        

        scada_set_command(crane_forward, true);
        Thread.sleep(2000);
        scada_set_command(crane_forward, false);
        Thread.sleep(4000);   

        
        scada_set_command(crane_down, true);
        Thread.sleep(3650);
        scada_set_command(crane_down, false);
        Thread.sleep(4000);      

        scada_set_command(crane_catch, true);
        Thread.sleep(2000);  
        
        
        scada_set_command(crane_up, true);
        Thread.sleep(4000);
        scada_set_command(crane_up, false);
       Thread.sleep(4000);  
        
        
        scada_set_command(crane_backward , true);
        Thread.sleep(2000);
        scada_set_command(crane_backward, false);
        Thread.sleep(4000);   
        
        
        scada_set_command(crane_right, true);
        Thread.sleep(4000);
        scada_set_command(crane_right, false);
       Thread.sleep(4000); 
        
        
        scada_set_command(crane_down, true);
        Thread.sleep(3650);
        scada_set_command(crane_down, false);
        Thread.sleep(4000);  
        
        scada_set_command(crane_catch, false);
        Thread.sleep(4000); 
        
        
        
    
        
        
        
        
        
        } catch (Exception e)
        {
            
            System.out.println("SCRIPT1 ERROR");
            
        }
        
    }
    
    
    
    
    public void scada_manual_connection()
    {
        try
        {
            modbus_manual_connection = new ModbusClient();
            modbus_manual_connection.Connect(PLC_IP_ADDRESS.getText(), 502);
            PLC_Debug_Log.append("Manual Control: Ready \n"); 
        
            PLC_STATUS_MSG.setText("READY");
            
        
        }
        catch (Exception e)
        {
            
             PLC_Debug_Log.append("Manual Control: Error! " + e.getLocalizedMessage() +  "\n" );
            
        }
        
        
    }
    
    
    public void scada_auto_script()
    {
        
        
        if (Button_Crane_Auto_Script_1.isSelected() == true)
        {
          
             
              
                PLC_Debug_Log.append("Scirpt Started");
                thrad_exit = false;
                t.start();
 
                        

                
    
                
        }
        else
        {

            thrad_exit = true;
            t.interrupt();
            PLC_Debug_Log.append("Scirpt stopped");
            
            initialize_thread();
       
            
        }
        
        

        
        
    }
    
    
    
    
    
    public void scada_set_command(int coil, Boolean command)
    {
        try
        {
        
            modbus_manual_connection.WriteSingleCoil(coil, command);
            modbus_manual_connection.WriteSingleCoil(coil, command);
            modbus_manual_connection.WriteSingleCoil(coil, command);
        }
        catch (Exception e)
        {
            
            PLC_Debug_Log.append("Write Command: Error, " + e.getLocalizedMessage());
            
        }
        
        
        
    }
    
    
    
    public void scada_test_connection()
    {
        try
        {
            
            ModbusClient modbus_connection = new ModbusClient(PLC_IP_ADDRESS.getText(), 502);
            
            modbus_connection.Connect();
            PLC_Debug_Log.append("Connection Test: Connect OK \n");
            
            
            
            boolean[] plc_coils = modbus_connection.ReadCoils(0, 8);
            PLC_Debug_Log.append("Connection Test: Read Coils OK \n");
            
            for (int i = 0 ; i < 8 ; i++)
            {
                
                if (plc_coils[i] == true)
                {
                    PLC_Debug_Log.append( " 1 " );
                    
                }
                else
                {
                    
                    PLC_Debug_Log.append( " 0 " );
                }
 
            }
             PLC_Debug_Log.append("\n");
            
            
                   
             
             
             
             
            modbus_connection.Disconnect();
            PLC_Debug_Log.append("Connection Test: Disconnect OK \n");
            
            
        }
        catch (Exception e)
        {
           
            PLC_Debug_Log.append("Connection Test: Error! " + e.getLocalizedMessage() +  "\n" );
           
            
            
        }
        
        
        
        
    }
    
    public void scada_reset_values()
    {
        
         try
        {
            
            ModbusClient modbus_connection = new ModbusClient(PLC_IP_ADDRESS.getText(), 502);
            
            modbus_connection.Connect();
            
            for (int i = 0 ; i < 8 ; i++)
            {
                modbus_connection.WriteSingleCoil( i, false);
                
                
            }
            PLC_Debug_Log.append("PLC RESET: Zeroize OK \n");
            
            
            
            boolean[] plc_coils = modbus_connection.ReadCoils(0, 8);
            PLC_Debug_Log.append("PLC RESET: Read Coils OK \n");
            
            for (int i = 0 ; i < 8 ; i++)
            {
                
                if (plc_coils[i] == true)
                {
                    PLC_Debug_Log.append( " 1 " );
                    
                }
                else
                {
                    
                    PLC_Debug_Log.append( " 0 " );
                }
 
            }
             PLC_Debug_Log.append("\n");
            
            
            modbus_connection.Disconnect();
            
            
            
        }
        catch (Exception e)
        {
           
            PLC_Debug_Log.append("PLC RESET: Error! " + e.getLocalizedMessage() +  "\n" );
           
            
            
        }
        
        
        
        
    }
    
    
    
    public void plc_left_operation()
    {
        
           if(Button_Crane_Left.isSelected() == true)
        {
            PLC_Debug_Log.append("LEFT GO \n");
            scada_set_command(crane_left, true);
        }
        else
        {
            PLC_Debug_Log.append("LEFT STOP \n");
             scada_set_command(crane_left, false);
        }
        
    }
           
    
    public void plc_right_operation()
    {
        
                
       if(Button_Crane_Right.isSelected() == true)
        {
            PLC_Debug_Log.append("RIGHT GO \n");
            scada_set_command(crane_right, true);
        }
        else
        {
            PLC_Debug_Log.append("RIGHT STOP \n");
             scada_set_command(crane_right, false);
        }
        
        
        
    }
    
    
      public void plc_front_operation()
    {
        
        if(Button_Crane_Forward.isSelected() == true)
        {
            PLC_Debug_Log.append("FORWARD GO \n");
            scada_set_command(crane_forward, true);
        }
        else
        {
            PLC_Debug_Log.append("FORWARD STOP \n");
             scada_set_command(crane_forward, false);
        }
         
        
        
    }
      
      
      public void plc_back_operation()
    {
        
        if(Button_Crane_Backward.isSelected() == true)
        {
            PLC_Debug_Log.append("BACKWARD GO \n");
            scada_set_command(crane_backward, true);
        }
        else
        {
            PLC_Debug_Log.append("BACKWARD STOP \n");
             scada_set_command(crane_backward, false);
        }
        
        
        
    }
      
      
      
      
    public void plc_up_operation()
    {
         
        if(Button_Crane_UP.isSelected() == true)
        {
            PLC_Debug_Log.append("UP GO \n");
            scada_set_command(crane_up, true);
        }
        else
        {
            PLC_Debug_Log.append("UP STOP \n");
             scada_set_command(crane_up, false);
        }
        
        
        
        
    }
      
    
    public void plc_down_operation()
    {
        
        if(Button_Crane_Down.isSelected() == true)
        {
            PLC_Debug_Log.append("DOWN GO \n");
            scada_set_command(crane_down, true);
        }
        else
        {
            PLC_Debug_Log.append("DOWN STOP \n");
             scada_set_command(crane_down, false);
        }
        
        
        
        
        
    }
    
    
    public void plc_catch_operation()
    {
        
       if (Button_Crane_Catch.isSelected())
       {
           PLC_Debug_Log.append("CATCH ON \n");
           scada_set_command(crane_catch, true);
       }
       else
       {
           PLC_Debug_Log.append("CATCH OFF \n");
           scada_set_command(crane_catch, false);
       }
       

    }
    
    
      
    
    public void plc_keyboard_operation(java.awt.event.KeyEvent evt , boolean pressed)
    {
        
        boolean go_right = false;
        boolean go_left = false;
   
        
           if ( modbus_manual_connection.isConnected() == false)
           {
               
               System.out.println("Not connected to PLC");
               return;
           }
        
         //System.out.print("Keyboard control enabled");


        // ================================================ PRAWO ================================================
         
         if  ((evt.getKeyCode() == 39 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
             PLC_Debug_Log.append("RIGHT GO \n");
             
              scada_set_command(crane_right, true);
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 39) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
             PLC_Debug_Log.append("RIGHT STOP \n");
             scada_set_command(crane_right, false);
             
             
             return;
             
         }

          // ================================================ LEWO  ================================================
         
            
            if  ((evt.getKeyCode() == 37 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
              PLC_Debug_Log.append("LEFT GO \n");
            scada_set_command(crane_left, true);
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 37) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
             PLC_Debug_Log.append("LEFT STOP \n");
            scada_set_command(crane_left, false);
             
             
             return;
             
         }         
        
         
         // ================================================ FRONT   ================================================ 
         
   
   if  ((evt.getKeyCode() == 38 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
             PLC_Debug_Log.append("FORWARD GO \n");
           scada_set_command(crane_forward, true);
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 38) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
            PLC_Debug_Log.append("FORWARD STOP \n");
            scada_set_command(crane_forward, false);
             
             
             return;
             
         }     
         
        // ================================================ BACK   ================================================  
         
         
         
         
         if  ((evt.getKeyCode() == 40 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
             PLC_Debug_Log.append("BACKWARD GO \n");
            scada_set_command(crane_backward, true);
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 40) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
            PLC_Debug_Log.append("BACKWARD STOP \n");
            scada_set_command(crane_backward, false);
             
             
             return;
             
         }     
            
             // ================================================ UP   ================================================  
         
         
         
         
         if  ((evt.getKeyCode() == 33 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
             PLC_Debug_Log.append("UP GO \n");
            scada_set_command(crane_up, true);
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 33 ) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
            PLC_Debug_Log.append("UP STOP \n");
        scada_set_command(crane_up, false);
             
             
             return;
             
         }     
                
      // ================================================ DOWN   ================================================  
         
         
         
         
         if  ((evt.getKeyCode() == 34 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
            PLC_Debug_Log.append("DOWN GO \n");
            scada_set_command(crane_down, true);
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 34 ) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
             PLC_Debug_Log.append("DOWN STOP \n");
           scada_set_command(crane_down, false);
             
             
             return;
             
         }        
         
         
   // ================================================ RESET   ================================================         
      
   
        if  ((evt.getKeyCode() == 127 ) && ( pressed == true)) // kliknalem pierwszy raz
         {
             
            PLC_Debug_Log.append("RESET \n");
            scada_reset_values();
             
             
             return;
             
         }
         
         
         if  ((evt.getKeyCode() == 127  ) && ( pressed == false)) // odkliknalem i odzznaczony
         {
             
         //    System.out.println("RESET ... ");
         
             
             
             return;
             
         }   
         
         // ================================================ magnez   ================================================   
         
         
              if  ((evt.getKeyCode() == 36 )) // kliknalem pierwszy raz
         {
             
            PLC_Debug_Log.append("Magnes ON \n");
        //    scada_reset_values();
              scada_set_command(crane_catch, true);
             
             return;
             
         }
              
          if  ((evt.getKeyCode() == 35 )) // kliknalem pierwszy raz
         {
             
            PLC_Debug_Log.append("Magnes OFF \n");
         //   scada_reset_values();
             scada_set_command(crane_catch, false);
             
             return;
             
         }
         
         
    }
    
            
    
    
  //  SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
//Date date = new Date(System.currentTimeMillis());
//System.out.println(formatter.format(date));
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        PLC_Debug_Log = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        PLC_STATUS_MSG = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        PLC_IP_ADDRESS = new javax.swing.JTextField();
        PLC_TEST_CONNECTION = new javax.swing.JButton();
        PLC_RESET_VALUES = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Button_Manual_Control = new javax.swing.JButton();
        Button_Crane_Left = new javax.swing.JToggleButton();
        Button_Crane_Right = new javax.swing.JToggleButton();
        Button_Crane_UP = new javax.swing.JToggleButton();
        Button_Crane_Down = new javax.swing.JToggleButton();
        Button_Crane_Forward = new javax.swing.JToggleButton();
        Button_Crane_Backward = new javax.swing.JToggleButton();
        Button_Crane_Auto_Script_1 = new javax.swing.JToggleButton();
        Button_Crane_Catch = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        panel_wanila = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1000, 900));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        PLC_Debug_Log.setColumns(20);
        PLC_Debug_Log.setRows(5);
        PLC_Debug_Log.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                PLC_Debug_LogKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(PLC_Debug_Log);

        jLabel1.setText("Status:");

        PLC_STATUS_MSG.setText("Not Connected");

        jLabel3.setText("PLC IP:");

        PLC_IP_ADDRESS.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        PLC_IP_ADDRESS.setText("192.168.30.50");

        PLC_TEST_CONNECTION.setText("TEST");
        PLC_TEST_CONNECTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PLC_TEST_CONNECTIONActionPerformed(evt);
            }
        });

        PLC_RESET_VALUES.setText("Reset");
        PLC_RESET_VALUES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PLC_RESET_VALUESActionPerformed(evt);
            }
        });

        jPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jPanel1KeyTyped(evt);
            }
        });

        Button_Manual_Control.setText("Manual Control");
        Button_Manual_Control.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Manual_ControlActionPerformed(evt);
            }
        });
        Button_Manual_Control.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                Button_Manual_ControlKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                Button_Manual_ControlKeyReleased(evt);
            }
        });

        Button_Crane_Left.setText("LEFT");
        Button_Crane_Left.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_LeftActionPerformed(evt);
            }
        });

        Button_Crane_Right.setText("RIGHT");
        Button_Crane_Right.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_RightActionPerformed(evt);
            }
        });

        Button_Crane_UP.setText("UP");
        Button_Crane_UP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_UPActionPerformed(evt);
            }
        });

        Button_Crane_Down.setText("DOWN");
        Button_Crane_Down.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_DownActionPerformed(evt);
            }
        });

        Button_Crane_Forward.setText("FRONT");
        Button_Crane_Forward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_ForwardActionPerformed(evt);
            }
        });

        Button_Crane_Backward.setText("BACK");
        Button_Crane_Backward.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_BackwardActionPerformed(evt);
            }
        });

        Button_Crane_Auto_Script_1.setText("Auto 1");
        Button_Crane_Auto_Script_1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_Auto_Script_1ActionPerformed(evt);
            }
        });

        Button_Crane_Catch.setText("CATCH");
        Button_Crane_Catch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_Crane_CatchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(Button_Manual_Control, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(Button_Crane_Forward, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(18, 18, 18)
                            .addComponent(Button_Crane_Backward, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(Button_Crane_UP, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Button_Crane_Left, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Button_Crane_Right, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Button_Crane_Down, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(Button_Crane_Auto_Script_1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Crane_Catch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Button_Manual_Control)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Button_Crane_Left, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Crane_Right, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Button_Crane_UP, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Crane_Down, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Button_Crane_Forward, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Button_Crane_Backward, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(Button_Crane_Catch, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 236, Short.MAX_VALUE)
                .addComponent(Button_Crane_Auto_Script_1)
                .addGap(37, 37, 37))
        );

        jButton1.setText("Start Camera");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        panel_wanila.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        panel_wanila.setPreferredSize(new java.awt.Dimension(640, 480));

        javax.swing.GroupLayout panel_wanilaLayout = new javax.swing.GroupLayout(panel_wanila);
        panel_wanila.setLayout(panel_wanilaLayout);
        panel_wanilaLayout.setHorizontalGroup(
            panel_wanilaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 634, Short.MAX_VALUE)
        );
        panel_wanilaLayout.setVerticalGroup(
            panel_wanilaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(PLC_STATUS_MSG)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(PLC_IP_ADDRESS, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PLC_TEST_CONNECTION)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PLC_RESET_VALUES)
                        .addGap(253, 253, 253)
                        .addComponent(jButton1)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel_wanila, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(PLC_IP_ADDRESS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(PLC_TEST_CONNECTION)
                            .addComponent(PLC_RESET_VALUES)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel_wanila, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(PLC_STATUS_MSG))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void PLC_TEST_CONNECTIONActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PLC_TEST_CONNECTIONActionPerformed
        
        scada_test_connection();
        
    }//GEN-LAST:event_PLC_TEST_CONNECTIONActionPerformed

    private void PLC_RESET_VALUESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PLC_RESET_VALUESActionPerformed

        scada_reset_values();
        
    }//GEN-LAST:event_PLC_RESET_VALUESActionPerformed

    private void Button_Manual_ControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Manual_ControlActionPerformed
        
         scada_manual_connection();
        
    }//GEN-LAST:event_Button_Manual_ControlActionPerformed

    private void Button_Crane_LeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_LeftActionPerformed
       
        
        plc_left_operation();
        
    }//GEN-LAST:event_Button_Crane_LeftActionPerformed

    private void Button_Crane_RightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_RightActionPerformed

        plc_right_operation();

        
    }//GEN-LAST:event_Button_Crane_RightActionPerformed

    private void Button_Crane_UPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_UPActionPerformed
      
        
        plc_up_operation();
        

        
        
        
    }//GEN-LAST:event_Button_Crane_UPActionPerformed

    private void Button_Crane_DownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_DownActionPerformed
        
        
        plc_down_operation();
        
 
        
        
    }//GEN-LAST:event_Button_Crane_DownActionPerformed

    private void Button_Crane_ForwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_ForwardActionPerformed
       
        
        
        plc_front_operation();
        
        
   
        
        
        
       
    }//GEN-LAST:event_Button_Crane_ForwardActionPerformed

    private void Button_Crane_BackwardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_BackwardActionPerformed
       
        plc_back_operation();
        
        

    }//GEN-LAST:event_Button_Crane_BackwardActionPerformed

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped
        
        
        
    }//GEN-LAST:event_formKeyTyped

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
  
    }//GEN-LAST:event_formKeyPressed

    private void jPanel1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPanel1KeyTyped
      
        
    }//GEN-LAST:event_jPanel1KeyTyped

    private void PLC_Debug_LogKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_PLC_Debug_LogKeyTyped
        
     
    }//GEN-LAST:event_PLC_Debug_LogKeyTyped

    private void Button_Manual_ControlKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Button_Manual_ControlKeyPressed
       
        
        plc_keyboard_operation(evt, true);
       // int code = evt.getKeyCode();
       // PLC_Debug_Log.append( Integer.toString(code));
        
        
    }//GEN-LAST:event_Button_Manual_ControlKeyPressed

    private void Button_Manual_ControlKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_Button_Manual_ControlKeyReleased
       
        plc_keyboard_operation(evt, false);
        
    }//GEN-LAST:event_Button_Manual_ControlKeyReleased

    private void Button_Crane_Auto_Script_1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_Auto_Script_1ActionPerformed
        
        scada_auto_script();
        
    }//GEN-LAST:event_Button_Crane_Auto_Script_1ActionPerformed

    private void Button_Crane_CatchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_Crane_CatchActionPerformed
      
        
        plc_catch_operation();
             
        
    }//GEN-LAST:event_Button_Crane_CatchActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
        
        start_camera();
        start_motion();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CrainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CrainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CrainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CrainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CrainGUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton Button_Crane_Auto_Script_1;
    private javax.swing.JToggleButton Button_Crane_Backward;
    private javax.swing.JToggleButton Button_Crane_Catch;
    private javax.swing.JToggleButton Button_Crane_Down;
    private javax.swing.JToggleButton Button_Crane_Forward;
    private javax.swing.JToggleButton Button_Crane_Left;
    private javax.swing.JToggleButton Button_Crane_Right;
    private javax.swing.JToggleButton Button_Crane_UP;
    private javax.swing.JButton Button_Manual_Control;
    private javax.swing.JTextArea PLC_Debug_Log;
    private javax.swing.JTextField PLC_IP_ADDRESS;
    private javax.swing.JButton PLC_RESET_VALUES;
    private javax.swing.JLabel PLC_STATUS_MSG;
    private javax.swing.JButton PLC_TEST_CONNECTION;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel panel_wanila;
    // End of variables declaration//GEN-END:variables
}
