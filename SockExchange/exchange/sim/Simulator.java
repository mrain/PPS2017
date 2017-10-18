package exchange.sim;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public class Simulator {
    private static final String root = "exchange";
    private static final String statics_root = "statics";

    private static long playerTimeout = 5000;
    private static boolean gui = false;
    private static double fps = 5;
    private static int n = 0;
    private static int p = 0;
    private static int t = 0;
    private static List<String> playerNames = new ArrayList<String>();
    private static PlayerWrapper[] players;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
//		args = new String[] {"-p", "g0", "g0", "g0", "", "-g"};
        parseArgs(args);
        players = new PlayerWrapper[p];
        for (int i = 0; i < p; ++ i) {
        	Log.record("Loading player " + i + ": " + playerNames.get(i));
        	Player player = loadPlayer(i, playerNames.get(i));
        	if (player == null) {
        		Log.record("Cannot load player " + i + ": " + playerNames.get(i));
        		System.exit(1);
        	}
        	players[i] = new PlayerWrapper(player, i, playerTimeout);
        }

        System.out.println("Starting game with " + p + " players");

        HTTPServer server = null;
        if (gui) {
        	server = new HTTPServer();
        	Log.record("Hosting HTTP Server on " + server.addr());
        	if (!Desktop.isDesktopSupported())
        		Log.record("Desktop operations not supported");
        	else if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
        		Log.record("Desktop browse operation not supported");
			else {
				try {
					Desktop.getDesktop().browse(new URI("http://localhost:" + server.port()));
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
        }

        // Simulation starts!
        List<Transaction> lastTransactions = new ArrayList<Transaction>();
        Offer[] offers = new Offer[p];
        Request[] requests = new Request[p];
        for (int turn = 1; turn <= t; ++ turn) {
            // Gather offers
            for (int i = 0; i < p; ++ i) {
                try {
                    offers[i] = players[i].makeOffer(Arrays.asList(requests), lastTransactions);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Getting requests
            for (int i = 0; i < p; ++ i) {
                List<Offer> toSend = new ArrayList<>();
                for (Offer offer : offers)
                    toSend.add(new Offer(offer));
                try {
                    requests[i] = players[i].requestExchange(toSend);
                    if (!validateRequest(offers, requests[i]))
                        throw new Exception(playerNames.get(i) + " making invalid requests");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            lastTransactions = ExchangeCenter.exchange(offers, requests);
            for (Transaction transaction : lastTransactions) {
                try {
                    players[transaction.getFirstID()].completeTransaction(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    players[transaction.getSecondID()].completeTransaction(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        for (int i = 0; i < p; ++ i) {
        	Log.record("Total running time for player " + i + " is " + players[i].getTotalElapsedTime() + "ms");
        }
        System.exit(0);
    }

    private static boolean validateRequest(Offer[] offers, Request request) {
        if (request == null)
            return false;
        if (request.getFirstOrderID() < -1 || request.getFirstOrderID() >= p)
            return false;
        if (request.getSecondOrderID() < -1 || request.getSecondOrderID() >= p)
            return false;
        if (request.getFirstOrderID() > -1) {
            if (request.getFirstOrderRank() < 1 || request.getFirstOrderRank() > 2)
                return false;
            else if (request.getFirstOrderRank() == 1) {
                if (offers[request.getFirstOrderID()].getFirst() == null)
                    return false;
            } else if (request.getFirstOrderRank() == 2) {
                if (offers[request.getFirstOrderID()].getSecond() == null)
                    return false;
            }
        }
        if (request.getSecondOrderID() > -1) {
            if (request.getSecondOrderRank() < 1 || request.getSecondOrderRank() > 2)
                return false;
            else if (request.getSecondOrderRank() == 1) {
                if (offers[request.getSecondOrderID()].getFirst() == null)
                    return false;
            } else if (request.getSecondOrderRank() == 2) {
                if (offers[request.getSecondOrderID()].getSecond() == null)
                    return false;
            }
        }
        return false;
    }

    private static String state(int n, List<String> playerNames, List<Integer>[] handles, double fps, int turn) {
    	// TODO
    	double refresh = 1000.0 / fps;
    	String ret = refresh + "," + turn + "," + n;
      for (int i = 0; i < n; ++ i)
        ret += "," + playerNames.get(i);
    	for (int i = 0; i < n; ++ i) {
    		ret += "," + handles[i].size();
    		for (Integer j : handles[i])
    			ret += "," + j;
    	}
    	return ret;
    }

    private static void gui(HTTPServer server, String content) {
    	if (server == null) return;
    	String path = null;
    	for (;;) {
    		for (;;) {
    			try {
    				path = server.request();
    				break;
    			} catch (IOException e) {
    				Log.record("HTTP request error " + e.getMessage());
    			}
    		}
    		if (path.equals("data.txt")) {
    			try {
    				server.reply(content);
    			} catch (IOException e) {
    				Log.record("HTTP dynamic reply error " + e.getMessage());
    			}
				return;
    		}
    		if (path.equals("")) path = "webpage.html";
    		else if (!Character.isLetter(path.charAt(0))) {
    			Log.record("Potentially malicious HTTP request \"" + path + "\"");
    			break;
    		}

    		File file = new File(statics_root + File.separator + path);
    		if (file == null) {
    			Log.record("Unknown HTTP request \"" + path + "\"");
    		} else {
    			try {
    				server.reply(file);
    			} catch (IOException e) {
    				Log.record("HTTP static reply error " + e.getMessage());
    			}
    		}
    	}
    }

    private static void parseArgs(String[] args) {
        int i = 0;
        for (; i < args.length; ++i) {
            switch (args[i].charAt(0)) {
                case '-':
                    if (args[i].equals("-p") || args[i].equals("--players")) {
                        while (i + 1 < args.length && args[i + 1].charAt(0) != '-') {
                            ++i;
                            playerNames.add(args[i]);
                        }
                    } else if (args[i].equals("-t")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing time limit");
                        }
                        t = Integer.parseInt(args[i]);
                    } else if (args[i].equals("-n")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing time limit");
                        }
                        n = Integer.parseInt(args[i]);
                    } else if (args[i].equals("-g") || args[i].equals("--gui")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing time limit");
                        }
                        playerTimeout = Integer.parseInt(args[i]);
                    } else if (args[i].equals("-tl") || args[i].equals("--timelimit")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing time limit");
                        }
                        playerTimeout = Integer.parseInt(args[i]);
                    } else if (args[i].equals("-l") || args[i].equals("--logfile")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing logfile name");
                        }
                        Log.setLogFile(args[i]);
                    } else if (args[i].equals("--fps")) {
                        if (++i == args.length) {
                            throw new IllegalArgumentException("Missing time limit");
                        }
                        fps = Double.parseDouble(args[i]);
                    } else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                    	Log.activate();
                    } else {
                        throw new IllegalArgumentException("Unknown argument \"" + args[i] + "\"");
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown argument \"" + args[i] + "\"");
            }
        }

        p = playerNames.size();
        if (playerNames.size() < 2) {
            throw new IllegalArgumentException("Not enough players, you need at least 2 player to start a game");
        }
        Log.record("Number of players: " + playerNames.size());
        Log.record("Time limit for each player: " + playerTimeout + "ms");
        Log.record("GUI " + (gui ? "enabled" : "disabled"));
        if (gui)
            Log.record("FPS: " + fps);
    }

    private static Set<File> directory(String path, String extension) {
        Set<File> files = new HashSet<File>();
        Set<File> prev_dirs = new HashSet<File>();
        prev_dirs.add(new File(path));
        do {
            Set<File> next_dirs = new HashSet<File>();
            for (File dir : prev_dirs)
                for (File file : dir.listFiles())
                    if (!file.canRead()) ;
                    else if (file.isDirectory())
                        next_dirs.add(file);
                    else if (file.getPath().endsWith(extension))
                        files.add(file);
            prev_dirs = next_dirs;
        } while (!prev_dirs.isEmpty());
        return files;
    }

    public static Player loadPlayer(int id, String name) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String sep = File.separator;
        Set<File> player_files = directory(root + sep + name, ".java");
        File class_file = new File(root + sep + name + sep + "Player.class");
        long class_modified = class_file.exists() ? class_file.lastModified() : -1;
        if (class_modified < 0 || class_modified < last_modified(player_files) ||
                class_modified < last_modified(directory(root + sep + "sim", ".java"))) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null)
                throw new IOException("Cannot find Java compiler");
            StandardJavaFileManager manager = compiler.
                    getStandardFileManager(null, null, null);
//            long files = player_files.size();
            Log.record("Compiling for player " + name);
            if (!compiler.getTask(null, manager, null, null, null,
                    manager.getJavaFileObjectsFromFiles(player_files)).call())
                throw new IOException("Compilation failed");
            class_file = new File(root + sep + name + sep + "Player.class");
            if (!class_file.exists())
                throw new FileNotFoundException("Missing class file");
        }
        ClassLoader loader = Simulator.class.getClassLoader();
        if (loader == null)
            throw new IOException("Cannot find Java class loader");
        @SuppressWarnings("rawtypes")
        Class<?> raw_class = loader.loadClass(root + "." + name + ".Player");
        Player player = null;
        try {
            Constructor<?> constructor = raw_class.getConstructor(Player.class);
            player = (Player)constructor.newInstance(id, n, p);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return player;
    }

    private static long last_modified(Iterable<File> files) {
        long last_date = 0;
        for (File file : files) {
            long date = file.lastModified();
            if (last_date < date)
                last_date = date;
        }
        return last_date;
    }
}
