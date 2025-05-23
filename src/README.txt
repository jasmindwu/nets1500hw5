Title: Payoff Matrices Explorer

Description: This is an application that allows the user to input the values of payoff matrices
and explore the effects of the different values on Nash Equilibrium and best responses.
The user can either work with a 2x2 or 3x3 payoff matrix, and our program will let the user know
what the pure strategy Nash Equilibria are, if they exist, along with calculating
the p and q values for a mixed-strategy Nash Equilibrium. Also, the user can input the player
and move they want to know the best response for, and we will interpret the payoff
matrices in the context of social relations for. Lastly, if a game has more than one pure nash
equilibrium, users can simulate a path from one nash equilibrium to another, given a starting 
equilibrium, a deviating player, and a deviating move. This can be interpreted in the context
of situations in which an initial nash equilibrium is “thrown off” by external factors. 


Categories:
Game Theory, Auctions, Matching Markets (payoff matrices and Nash Equilibria)
Social Networks (exploring the motivation behind interconnectivity based off of different payoff values)

Work breakdown:
Jasmin: PayoffMatrix class, Main class logic (1-3, creating payoff matrix)
Lihini: MainGUI, stimulateDeviationPath in PayoffMatrix, logic behind analyzing
equilibrium transition paths
