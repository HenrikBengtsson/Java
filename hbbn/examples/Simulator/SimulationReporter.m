% SimulationReporter.m
function SimulationReporter(varargin)
  args = varargin;
  argn = nargin;

  datfile = 'simulation.dat';
  if (argn > 0)
    datfile = args{1};  
  end
  fprintf(1, 'Loading "%s"...', datfile);
  simulation = load(datfile);
  fprintf(1, 'ready\n');
  n = size(simulation,1);
  varn = size(simulation,2);

  for i=1:varn,
  	variable.nbrOfStates = 0;

    variable.name         = sprintf('var[%d]', i);
		variable.data         = simulation(:,i);
    if (variable.nbrOfStates <= 0)
  		variable.nbrOfStates = max(variable.data)+1;
    end
    variable.count        = variableCount(variable);
    variable.distribution = variable.count/length(variable.data);
    variablePrint(variable);
  end

  fprintf(1,'Based on %d instance with %d values each.\n', n, varn);
 

function s = vectorToString(vector)
  len = length(vector);
  if (len == 0)
    s = '[]';
    return
  end
  s = sprintf('[%.3f', vector(1));
  for k=2:len,
    s = [s sprintf(' %.3f', vector(k))];
  end
  s = [s ']'];

function variablePrint(variable)
  fprintf(1, '%s : %s\n', variable.name, vectorToString(variable.distribution));

function count = variableCount(variable)
  n = variable.nbrOfStates;
  count = zeros(1,n);
  for k=1:n, 
    count(k) = sum(variable.data == k-1);
  end
  
