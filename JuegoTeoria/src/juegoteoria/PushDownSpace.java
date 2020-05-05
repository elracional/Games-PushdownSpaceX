/*
 * Clase principal donde corre todo el juego
 */
package juegoteoria;
    import java.util.Random;
    import java.awt.*; 
    import java.awt.event.*;
    import javax.swing.*;
    import java.util.Stack;
/**
 * @author Arturo Vargas
 */
public class PushDownSpace extends JFrame implements KeyListener,Runnable{
    //Variables para determinar el inicio y el fin del juego 
    boolean juegoCorriendo = false; //Determina si inicio el juego
    boolean gameOver = false;       //Detemina si se termino el juego
    int causaGameOver;    
    //Interfaz gráfica del juego
    JPanel panelJuego;
    JLabel fondoJuego;
    ImageIcon fondoDelJuego;   
    //Lambdas
    JLabel lambda[];
    int cantidadLambdas;
    int coordenadasLambda[][];
    //Informacion del juego
    JLabel lblCoordenadas;
    JLabel lblTime;     JLabel lblTiempo;
    JLabel lblScore;    JLabel lblPuntos;
    JLabel lblLives;    JLabel lblVidas;  
    //Variables para las naves enemigas
    JLabel malo[];
    Thread malo1,malo2,malo3,malo4;
    int xMalo[]; int movimientoXMalo[];
    int yMalo[]; int movimientoYMalo[];
    int delayMalo;
    boolean correMalo = false;
    //Pila del juego
    Stack<String> pila;
    JLabel vistaPila[];
    int cantidadPila;
    int yPila = 645;
    int xPila = 630;
    //Datos del juego actual
    String nombreJugador;
    int vidas;
    int puntaje;
    int tiempo;
    //Hilos para el timer y los enemigos
    Thread timer;
    Thread missile;
    //Datos para el misil
    JLabel misil;
    boolean impacto;
    int xMisil;
    int yMisil;
    int movimientoXMisil;
    int movimientoYMisil;
    //Personaje del jugador(Nave)
    JLabel ship;
    ImageIcon nave;
    int direction;
    //Coordenadas iniciales de la nave
    int x = 285;
    int y = 600;
    //Velocidades en cada eje y la general
    int movimientoX;
    int movimientoY;
    int velocidad;
    //COnstructor donde se definen datos del juego acutal
    public PushDownSpace(){
        cantidadLambdas = 4;
        velocidad = 30;
        tiempo = 220;
        vidas = 3;
        puntaje = 0;
        delayMalo = 600;
        //Se inicializa la pila
        pila = new Stack<String>(); 
        //Se crea la nave
        ship = new JLabel();
        ship.setIcon(new ImageIcon(getClass().getResource("/Imagen/nave.png")));
        //Se crea el misil
        misil = new JLabel();
        misil.setIcon(new ImageIcon(getClass().getResource("/Imagen/misil.png")));
        xMisil = 615; yMisil = 240;
        //Se inicializan las JLabel de las Lambdas
        lambda = new JLabel[cantidadLambdas];
        coordenadasLambda = new int[cantidadLambdas][3];//X,Y,Activo
        for(int i = 0; i< cantidadLambdas; i++){
            lambda[i] = new JLabel();
            relocalizaLambda(i);
        }
        //Se crean los labels de la pila 
        vistaPila = new JLabel[6];
        for(int i=0;i<6;i++){
            vistaPila[i] = new JLabel();
        }
        //Se le ingresan 3 elementos a la pila
        for(int i = 0;i<3;i++){
            pushPila();
        }
        //Se crean los labels de las naves enemigas
        malo = new JLabel[3];
        xMalo = new int[3]; movimientoXMalo = new int[3];
        yMalo = new int[3]; movimientoYMalo = new int[3];
        for(int i = 0 ;i<3; i++){
            malo[i] = new JLabel();
        }
        //Labels datos del jugador
        lblCoordenadas  =   new JLabel();
        lblPuntos       =   new JLabel();
        lblTiempo       =   new JLabel();
        lblVidas        =   new JLabel();
    }
    //Método para crear la interfaz de usuario
    public void crearGUI(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container ventana = getContentPane();
        //Panel del juego donde se dibujará todo
        panelJuego = new JPanel();
        panelJuego.setLayout(null);
        panelJuego.setBounds(0,0,ventana.getWidth(),ventana.getHeight());
        
        fondoJuego = new JLabel();
        fondoJuego.setIcon(new ImageIcon(getClass().getResource("/Imagen/fondo.jpg")));
        fondoJuego.setBounds(0,0,900,700);
        fondoJuego.setVisible(true);
        panelJuego.add(fondoJuego);
        
        hacerTablero();
        
        //Tiempo
        lblTime   = new JLabel("Tiempo:");
            lblTime.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblTime.setBounds(630,100,90,30);
            panelJuego.add(lblTime,1);
        lblTiempo = new JLabel("0s");
            lblTiempo.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblTiempo.setBounds(750,100,120,30);
            panelJuego.add(lblTiempo,0);
        //Puntaje
        lblScore = new JLabel("Puntaje:");
            lblScore.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblScore.setBounds(630,140,90,30);
            panelJuego.add(lblScore,1);
        lblPuntos = new JLabel("0");
            lblPuntos.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblPuntos.setBounds(750,140,90,30);
            lblPuntos.setVisible(true);
            panelJuego.add(lblPuntos,1);
        //Vidas
        lblLives = new JLabel("Vidas:");
            lblLives.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblLives.setBounds(630,180,90,30);
            panelJuego.add(lblLives,1);
        lblVidas = new JLabel("0");
            lblVidas.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblVidas.setBounds(750,180,90,30);
            panelJuego.add(lblVidas,1);
        //Coordenadas
        lblCoordenadas.setText("X: 0 Y: 0");
            lblCoordenadas.setFont(new java.awt.Font("Adobe Heiti Std R", 1, 20));
            lblCoordenadas.setBounds(15,30,150,30);
            lblCoordenadas.setVisible(true);
            panelJuego.add(lblCoordenadas,1);
        //Se hace visible y se agrega el panel al frame
        panelJuego.setVisible(true);
        ventana.add(panelJuego);
        addKeyListener(this);//Se suscribe a KeyListener para que se hagan acciones con las acciones del usuario por el teclado
    }
    //Metoto con la que se dibuja el tablero
    public void hacerTablero(){
        for(int i= 0; i < 20; i++){ //Se dibuja el marco donde se ejecutará el juego
            JLabel paredDerecha = new JLabel();
            paredDerecha.setIcon(new ImageIcon(getClass().getResource("/Imagen/1.png")));
            paredDerecha.setBounds(585,60+(i*30),30,30);//localizacion del bloque
            paredDerecha.setVisible(true);
            panelJuego.add(paredDerecha,0);
            
            JLabel paredIzquierda = new JLabel();
            paredIzquierda.setIcon(new ImageIcon(getClass().getResource("/Imagen/1.png")));
            paredIzquierda.setBounds(15,60+(i*30),30,30);//localizacion del bloque
            paredIzquierda.setVisible(true);
            panelJuego.add(paredIzquierda,0);
            
            JLabel paredAbajo = new JLabel();
            paredAbajo.setIcon(new ImageIcon(getClass().getResource("/Imagen/1.png")));
            paredAbajo.setBounds(15+(i*30),660,30,30);//localizacion del bloque
            paredAbajo.setVisible(true);
            panelJuego.add(paredAbajo,0);
            
            JLabel paredArriba = new JLabel();
            paredArriba.setIcon(new ImageIcon(getClass().getResource("/Imagen/1.png")));
            paredArriba.setBounds(15+(i*30),60,30,30);//localizacion del bloque
            paredArriba.setVisible(true);
            panelJuego.add(paredArriba,0);
        }
        //Se dibuja el fondo del juego
        for(int i = 1; i <= 18; i++){
            for(int j= 1; j <= 19; j++){
                JLabel fondo = new JLabel();
                int espacio = new Random().nextInt(4)+1;
                String ruta = "";
                switch(espacio){//Se selecciona uno de los cuatro fondos
                    case 1:
                        ruta = "/Imagen/espacio1.png";
                        break;
                    case 2:
                        ruta = "/Imagen/espacio2.png";
                        break;
                    case 3:
                        ruta = "/Imagen/espacio3.png";
                        break;
                    case 4:
                        ruta = "/Imagen/espacio4.png";
                        break;
                }
                fondo.setIcon(new ImageIcon(getClass().getResource(ruta)));              
                fondo.setVisible(true);
                fondo.setBounds(15+(i*30),60+(j*30),30,30);
                panelJuego.add(fondo,0);
            }
        }
    }
    //Metodo para hacer validaciones de objetos recogibles y choques con enemigos
    public void validaciones(){
        int validaX,validaY; //holders para validaciones
        //Si el jugador va a pasar por una pared, se regresa a la posición anterior
        if(x == 15 || x == 585|| y == 660 || y == 60){
            x -= movimientoX;
            y -= movimientoY;
        }
        //Se valida si el jugador se ha encontrado a un lambda
        for(int i = 0;i<cantidadLambdas;i++){
            validaX = 15+(coordenadasLambda[i][0]*30);
            validaY = 60+(coordenadasLambda[i][1]*30);
            if(x == validaX && y == validaY){
                relocalizaLambda(i);
                popPila();
            }
        }
        //Si ya no quedan vidas se termina el juego
        if(vidas==0){
            gameOver = true;
        }
    }
    //Metodo para acomodar una lambda en algún lugar del tablero
    public void relocalizaLambda(int cualLambda){
        int coordX = new Random().nextInt(18)+1;
        int coordY = new Random().nextInt(18)+1;        
        coordenadasLambda[cualLambda][0] = coordX;
        coordenadasLambda[cualLambda][1] = coordY;
        coordenadasLambda[cualLambda][2] = 1;
    }
    //Pinta elementos sobre un panel
    public void pintar(JPanel pintame){//Recibe el panel del juego
        //Se le suman a las coordenadas del raton para que avanze en la direccion establecida previamente
        x += movimientoX;
        y += movimientoY;
        //Se hacen validaciones de posición antes de pintar 
        validaciones();
        //Se pintan las lambdas
        for(int i = 0;i<cantidadLambdas;i++){
            lambda[i].setIcon(new ImageIcon(getClass().getResource("/Imagen/lambda.png")));
            lambda[i].setBounds(15+(coordenadasLambda[i][0]*30),60+(coordenadasLambda[i][1]*30),30,30);
            lambda[i].setVisible(true);
            pintame.add(lambda[i],1);
        }
        //Se dibuja la pila como se encuentre actualmente
        int lugarPila = 0;
        int elementoPila = yPila;
        for(int i = 1 ;i <= 6;i++){
            if(i <= cantidadPila){
                vistaPila[lugarPila].setIcon(new ImageIcon(getClass().getResource("/Imagen/ovni.png")));
            }else{
                vistaPila[lugarPila].setIcon(new ImageIcon(getClass().getResource("/Imagen/0.png")));
            }
            vistaPila[lugarPila].setBounds(xPila,elementoPila,30,30); 
            vistaPila[lugarPila].setVisible(true);
            pintame.add(vistaPila[lugarPila],1);
            lugarPila++; elementoPila-=60;
        }
        //Puntos    
        lblPuntos.setText(Integer.toString(puntaje));
        //VIdas
        lblVidas.setText(Integer.toString(vidas));
        //Coordenadas
        lblCoordenadas.setText("X: "+Integer.toString(x)+" Y: "+Integer.toString(y));
        //Se establecen las coordenadas y se pinta el raton
        switch(direction){
            case 1://Arriba
                ship.setIcon(new ImageIcon(getClass().getResource("/Imagen/naveArriba.png")));
                break;
            case 2://Abajo
                ship.setIcon(new ImageIcon(getClass().getResource("/Imagen/naveAbajo.png")));
                break;
            case 3://Derecha
                ship.setIcon(new ImageIcon(getClass().getResource("/Imagen/naveDerecha.png")));
                break;
            case 4://Izquierda 
                ship.setIcon(new ImageIcon(getClass().getResource("/Imagen/naveIzquierda.png")));
                break;        
        }
        ship.setBounds(x, y, 30, 30);
        ship.setVisible(true);
        pintame.add(ship,1);
    }
    //Metodo con el que se agrega un elemento a la pila
    public void pushPila(){
        if(cantidadPila == 6){//Si se llega al limite establecido se restan puntos
            puntaje -= 5;
        }else{
           pila.push("X");
            cantidadPila++; 
        }   
    }
    //Metodo con el que se elimina un elemnto de la pila
    public void popPila(){
        if(pila.empty()){
            //Se restan puntos si la pila estaba vacía al momento de hacer pop
            puntaje -= 5;
        }else{//Cada que se logra hacer pop se aumentan 10 puntos 
           cantidadPila--;
           puntaje += 10;
           pila.pop(); 
        }
    }
    //----------------Cambios de dirección de la nave----------------//
    public void quieto(){
        movimientoY = 0;
        movimientoX = 0;
    }
    public void arriba(){
        movimientoY = -velocidad;
        movimientoX = 0;
        direction = 1;
    }
    public void abajo(){
        movimientoY =  velocidad;
        movimientoX = 0;
        direction = 2;
    }
    public void derecha(){
        movimientoY = 0;
        movimientoX =  velocidad;
        direction = 3;
    }
    public void izquierda(){
        movimientoY = 0;
        movimientoX = -velocidad;
        direction = 4;
    }
    
     //Metodo sobreescrito de KeyListener para que responda a las acciones del usuario
    public void keyPressed(KeyEvent e){
        //Se obtiene la tecla que se presiono
        int tecla = e.getKeyCode();
        if(juegoCorriendo == false){//Si el juego no ha iniciado, comienzan a correr los hilos y el juego
            if(tecla == KeyEvent.VK_S){
                quieto();
                juegoCorriendo = true;//Se vuelve verdadera la variable para comenzar el juego
                correMalo = true;
                //Se inicializan los hilos
                timer = new Thread(this,"Timer");
                timer.start();
                
                malo1 = new Thread(this,"Malo1");
                malo2 = new Thread(this,"Malo2");
                malo3 = new Thread(this,"Malo3");
                
                misil.setBounds(xMisil,yMisil,30,30);
                misil.setVisible(true);
                panelJuego.add(misil,0);
                for(int i=0;i<3;i++){
                   iniciaMalo(i);
                }
                malo1.start();
                malo2.start();
                malo3.start();
            }
        }
        //Si la variable de juego inciado es verdadera se empezara a "escuchar" las teclas de las flechas
        if(juegoCorriendo){
            //Si se presiona otra tecla ademas de las flechas no se hace nada
            if(tecla != KeyEvent.VK_UP || tecla != KeyEvent.VK_DOWN || tecla != KeyEvent.VK_LEFT || tecla != KeyEvent.VK_RIGHT){
                quieto();
                pintar(panelJuego);
            }
            if(tecla == KeyEvent.VK_D){//Se dispara un misil
                missile = new Thread(this,"Misil");
                movimientoXMisil = 0; movimientoYMisil = 0;
                missile.start();
            }
            //Teclas para llenar y vaciar la pila para hacer "debug" o pruebas
            if(tecla == KeyEvent.VK_A){
                pushPila();
            }
            if(tecla == KeyEvent.VK_Q){
                popPila();
            }
            //Dependiendo la tecla se cambia la direccion del raton
            if(tecla == KeyEvent.VK_UP){//Arriba
                arriba();
            }
            if(tecla == KeyEvent.VK_DOWN){//Abajo
                abajo();
            }
            if(tecla == KeyEvent.VK_RIGHT){//Derecha
                derecha();
            }
            if(tecla == KeyEvent.VK_LEFT){//Izquierda
                izquierda();
            }
            //Despues de cambiar la direccion se pintan los demaas elementos
            pintar(panelJuego);
            //Se pasa a la pantalla final cuando se termina el juego
            if(gameOver){
                nombreJugador = JOptionPane.showInputDialog("Juego Terminado ingresa tu nombre ");
                this.dispose();//Se cierra la pantalla anterior
                PantallaFinal resultados = new PantallaFinal();
                resultados.preparaDatos(nombreJugador,tiempo, puntaje);
                resultados.setVisible(true); 
            }
        }
    }
    public void keyTyped(KeyEvent e){}
    public void keyReleased(KeyEvent e){}
    
    public void iniciaMalo(int cualMalo){
        int xBad = new Random().nextInt(18)+1 , yBad = new Random().nextInt(18)+1;
        xMalo[cualMalo] = 15 + (xBad * 30);
        yMalo[cualMalo] = 60 + (yBad * 30);
        movimientoXMalo[cualMalo] = velocidad;
        movimientoYMalo[cualMalo] = 0;
        malo[cualMalo].setIcon(new ImageIcon(getClass().getResource("/Imagen/ovni.png")));
    }
    //Metodo para mover a la nave enemiga
    private void moverMalo(int cualMalo){
        int direccion = 0;
        //Se revisa si lo alcanzo un misil 
        if(xMisil == xMalo[cualMalo] && yMisil == yMalo[cualMalo]){
            impacto = true;
            pushPila();
            int xBad = new Random().nextInt(18)+1 , yBad = new Random().nextInt(18)+1;
            xMalo[cualMalo] = 15 + (xBad * 30);
            yMalo[cualMalo] = 60 + (yBad * 30);
        }
        if(x == xMalo[cualMalo] && y == yMalo[cualMalo]){
            vidas--;
            x = 285;
            y = 600;
        }
        //Se determina a que dirección conviene mas moverse
        if(((x - xMalo[cualMalo]) < (y - yMalo[cualMalo]))){
            //Se debe de mover en el eje X
            if(x > xMalo[cualMalo]){
                direccion = 4;
            }else{
                direccion = 3;
            }
        }
        if(((x - xMalo[cualMalo]) > (y - yMalo[cualMalo]))){
            //Se debe de mover en el eje Y
            if(y > yMalo[cualMalo]){
                direccion = 2;
            }else{
                direccion = 1;
            }
        }
        switch(direccion){//Dependiendo la variable aleatoria cambiara su direccion
            //Arriba
            case 1:
                movimientoYMalo[cualMalo] -= velocidad;
                movimientoXMalo[cualMalo] = 0;
                break;
            //Abajo
            case 2:
                movimientoYMalo[cualMalo] =  velocidad;
                movimientoXMalo[cualMalo] = 0;
                break;
            //Izquierda
            case 3:
                movimientoYMalo[cualMalo] = 0;
                movimientoXMalo[cualMalo] = -velocidad;
                break;
            //Derecha
            case 4:
                movimientoYMalo[cualMalo] = 0;
                movimientoXMalo[cualMalo] =  velocidad;
                break;
        }
        
        xMalo[cualMalo] += movimientoXMalo[cualMalo];
        yMalo[cualMalo] += movimientoYMalo[cualMalo];
        
        if(xMalo[cualMalo] == 15 || xMalo[cualMalo] == 585|| yMalo[cualMalo] == 660 || yMalo[cualMalo] == 60){
            xMalo[cualMalo] -= movimientoX;
            yMalo[cualMalo] -= movimientoY;
        }
        dibujarMalo(panelJuego,cualMalo);        
    }
    //Se pinta el gato sobre el laberinto        
    private void dibujarMalo(JPanel pintame,int cualMalo){
        malo[cualMalo].setBounds(xMalo[cualMalo], yMalo[cualMalo], 30, 30);
        malo[cualMalo].setVisible(true);
        //panelJuego.add(malo[cualMalo],0);
        pintame.add(malo[cualMalo],1);
        validaciones();
    }
    private void retrasarMalo(){
        try{
            Thread.sleep(delayMalo);
        }catch(InterruptedException e){}
    }
    public void run(){
        String nombreHilo = Thread.currentThread().getName();
        //SI el hilo generado es el timer se empieza a correr el tiempo
        if(nombreHilo.equals("Timer")){
            try{
                while(tiempo>0)
                {
                    //Se cambia el label por el tiempo que queda
                    lblTiempo.setText(Integer.toString(tiempo)+"s   ");
                    timer.sleep(1000);// se detiene el hilo por 1 segundo
                    tiempo--;//Se resta tiempo
                }
                gameOver = true;
            }catch(Exception e){
                
            }
        }else if(nombreHilo.equals("Misil")){
            try{
                impacto = false;
                xMisil = x; yMisil = y;
                switch(direction){
                    case 1://Arriba
                        movimientoXMisil = 0;
                        movimientoYMisil -= velocidad;
                        misil.setIcon(new ImageIcon(getClass().getResource("/Imagen/misilArriba.png")));
                        break;
                    case 2://Abajo
                        movimientoXMisil = 0;
                        movimientoYMisil += velocidad;
                        misil.setIcon(new ImageIcon(getClass().getResource("/Imagen/misilAbajo.png")));
                        break;
                    case 3://Derecha
                        movimientoXMisil += velocidad;
                        movimientoYMisil = 0;
                        misil.setIcon(new ImageIcon(getClass().getResource("/Imagen/misilDerecha.png")));
                        break;
                    case 4://Izquierda 
                        movimientoXMisil -= velocidad;
                        movimientoYMisil = 0;
                        misil.setIcon(new ImageIcon(getClass().getResource("/Imagen/misilIzquierda.png")));
                        break;    
                }
                do{
                    xMisil += movimientoXMisil;
                    yMisil += movimientoYMisil;
                    for(int i = 0;i<3;i++){
                        if(xMisil == xMalo[i] && yMisil == yMalo[i]){
                            impacto = true;
                            break;
                        }
                    }
                    if(xMisil == 15 || xMisil == 585|| yMisil == 660 || yMisil == 60){
                        break;
                    }
                    misil.setBounds(xMisil,yMisil,30,30);
                    misil.setVisible(true);
                    panelJuego.add(misil,0);
                    missile.sleep(500);
                }while(!impacto);
                //Se "Muestra el icono del misil" indicando que se puede disparar uno nuevo
                xMisil = 615; yMisil = 240;
                impacto = false;
                movimientoXMisil = 0; movimientoYMisil = 0;
                misil.setBounds(xMisil,yMisil,30,30);
                misil.setVisible(true);
                panelJuego.add(misil,0);
            }catch(Exception e){
                
            }
        }else{
            while(correMalo){
                if(nombreHilo.equals("Malo1")){
                    while(!gameOver){
                        moverMalo(0);
                        dibujarMalo(panelJuego,0);
                        retrasarMalo();
                    }
                }
                if(nombreHilo.equals("Malo2")){
                    while(!gameOver){
                        moverMalo(1);
                        dibujarMalo(panelJuego,1);
                        retrasarMalo();
                    }
                }
                if(nombreHilo.equals("Malo3")){
                    while(!gameOver){
                        moverMalo(2);
                        dibujarMalo(panelJuego,2);
                        retrasarMalo();
                    }
                } 
            }
        }
    }
}
