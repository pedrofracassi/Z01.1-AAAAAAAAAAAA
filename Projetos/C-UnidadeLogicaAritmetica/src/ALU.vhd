-- Elementos de Sistemas
-- by Luciano Soares
-- ALU.vhd

-- Unidade Lógica Aritmética (ULA)
-- Recebe dois valores de 16bits e
-- calcula uma das seguintes funções:
-- X+y, x-y, y-x, 0, 1, -1, x, y, -x, -y,
-- X+1, y+1, x-1, y-1, x&y, x|y
-- De acordo com os 6 bits de entrada denotados:
-- zx, nx, zy, ny, f, no.
-- Também calcula duas saídas de 1 bit:
-- Se a saída == 0, zr é definida como 1, senão 0;
-- Se a saída <0, ng é definida como 1, senão 0.
-- a ULA opera sobre os valores, da seguinte forma:
-- se (zx == 1) então x = 0
-- se (nx == 1) então x =! X
-- se (zy == 1) então y = 0
-- se (ny == 1) então y =! Y
-- se (f == 1) então saída = x + y
-- se (f == 0) então saída = x & y
-- se (no == 1) então saída = !saída
-- se (out == 0) então zr = 1
-- se (out <0) então ng = 1

library IEEE;
use IEEE.STD_LOGIC_1164.ALL;

entity ALU is
	port (
			x,y:   in STD_LOGIC_VECTOR(15 downto 0); -- entradas de dados da ALU
			zx:    in STD_LOGIC;                     -- zera a entrada x
			nx:    in STD_LOGIC;                     -- inverte a entrada x
			zy:    in STD_LOGIC;                     -- zera a entrada y
			ny:    in STD_LOGIC;                     -- inverte a entrada y
			f:     in STD_LOGIC_VECTOR(1 downto 0);                     -- se 0 calcula x & y, senão x + y
			no:    in STD_LOGIC;                     -- inverte o valor da saída
			dir:   in STD_LOGIC;                     -- bit de controle direção do shifter
			zr:    out STD_LOGIC;                    -- setado se saída igual a zero
			ng:    out STD_LOGIC;                    -- setado se saída é negativa
			saida: out STD_LOGIC_VECTOR(15 downto 0); -- saída de dados da ALU
			carry: out STD_LOGIC                      -- saida com o carry do adder
	);
end entity;

architecture  rtl OF alu is
  -- Aqui declaramos sinais (fios auxiliares)
  -- e componentes (outros módulos) que serao
  -- utilizados nesse modulo.

	component zerador16 IS
		port(z   : in STD_LOGIC;
			   a   : in STD_LOGIC_VECTOR(15 downto 0);
			   y   : out STD_LOGIC_VECTOR(15 downto 0)
			);
	end component;

	component inversor16 is
		port(z   : in STD_LOGIC;
			   a   : in STD_LOGIC_VECTOR(15 downto 0);
			   y   : out STD_LOGIC_VECTOR(15 downto 0)
		);
	end component;

	component Add16 is
		port(
			a   :  in STD_LOGIC_VECTOR(15 downto 0);
			b   :  in STD_LOGIC_VECTOR(15 downto 0);
			q   : out STD_LOGIC_VECTOR(15 downto 0);
			carry : out STD_LOGIC_VECTOR(14 downto 0)
		);
	end component;

	component And16 is
		port (
			a:   in  STD_LOGIC_VECTOR(15 downto 0);
			b:   in  STD_LOGIC_VECTOR(15 downto 0);
			q:   out STD_LOGIC_VECTOR(15 downto 0)
		);
	end component;

	component comparador16 is
		port(
			a   : in STD_LOGIC_VECTOR(15 downto 0);
			zr   : out STD_LOGIC;
			ng   : out STD_LOGIC
    );
	end component;

	component Mux4Way16 is
		port (
			a:   in  STD_LOGIC_VECTOR(15 downto 0);
			b:   in  STD_LOGIC_VECTOR(15 downto 0);
			c:   in  STD_LOGIC_VECTOR(15 downto 0);
			d:   in  STD_LOGIC_VECTOR(15 downto 0);
			sel: in  STD_LOGIC_VECTOR(1 downto 0);
			q:   out STD_LOGIC_VECTOR(15 downto 0)
		);
	end component;
	
	component Xor is
		port(
			a:   in  STD_LOGIC_VECTOR(15 downto 0);
			b:   in  STD_LOGIC_VECTOR(15 downto 0);
			q:   out STD_LOGIC_VECTOR(15 downto 0)
		);
	end component;

	component BarrelShifter16 is
		port(
			a:    in  STD_LOGIC_VECTOR(15 downto 0);  
			dir:  in  std_logic;                      
			size: in  std_logic_vector(2 downto 0);   
			q:    out STD_LOGIC_VECTOR(15 downto 0));  
		); 
	end component;

   SIGNAL
	 		zxout,
			zyout,
			nxout,
			nyout,
			andout,
			adderout,
			muxout,
			precomp: std_logic_vector(15 downto 0);

begin
  -- Implementação vem aqui!

	zeradorx: zerador16 port map(
		z => zx,
		a => x,
		y => zxout
	);

	zeradory: zerador16 port map(
		z => zy,
		a => y,
		y => zyout
	);

	inversorx: inversor16 port map(
		z => nx,
		a => zxout,
		y => nxout
	);

	inversory: inversor16 port map(
		z => ny,
		a => zyout,
		y => nyout
	);

	adderxy: Add16 port map(
		a => nxout,
		b => nyout,
		q => adderout
	);

	carry <= when (nxout(15) xnor nyout(15)) and (nxout(15) xor adderout(15));

	andxy: And16 port map(
		a => nxout,
		b => nyout,
		q => andout
	);

	mux: Mux4Way16 port map(
		a => andout,
		b => adderout,
		c => xorout,
		d => shiftout,
		sel => f,
		q => muxout
	);

	inversormux: inversor16 port map(
		z => no,
		a => muxout,
		y => saida
	);

	comparador: comparador16 port map(
		a => saida,
		zr => zr,
		ng => ng
	);

	xor: Xor port map(
		a => nxout
		b => nyout
		q => xorout

	);

	shift: BarrelShifter16 port map(
		a => nxout
		dir => dir
		size => nyout(2 downto 0)
		q => shiftout
	);

end architecture;
