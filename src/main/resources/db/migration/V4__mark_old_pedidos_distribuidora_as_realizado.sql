-- Mark old PedidoDistribuidora records as realizado=true
-- when ALL their items are already pendiente=false (confirmed in the old flow).

UPDATE pd_pedido_a_distribuidora
SET pd_pedido_realizado = true
WHERE pd_pedido_realizado = false
  AND pd_pedido_a_distribuidora_k NOT IN (
    SELECT DISTINCT pdpi.pdpi_pedido_a_distribuidora_pd
    FROM pdpi_pedido_distribuidora_item pdpi
    JOIN pi_pedido_item pi ON pi.pi_pedido_item_k = pdpi.pdpi_pedido_item_pi
    WHERE pi.pi_pendiente = true
  );

-- Migrate from ManyToMany join table to direct FK (1 PedidoDistribuidora per item).
-- Step 1: Add the new FK column
ALTER TABLE pd_pedido_a_distribuidora ADD COLUMN pd_pedido_item_pi INT;

-- Step 2: For each PD, pick the first item as the direct FK
UPDATE pd_pedido_a_distribuidora pd
SET pd_pedido_item_pi = (
    SELECT MIN(pdpi_pedido_item_pi)
    FROM pdpi_pedido_distribuidora_item
    WHERE pdpi_pedido_a_distribuidora_pd = pd.pd_pedido_a_distribuidora_k
);

-- Step 3: For PDs that had multiple items, create new rows for the extra items
INSERT INTO pd_pedido_a_distribuidora (pd_fecha, pd_distribuidora_ed, pd_pedido_realizado, pd_pedido_item_pi)
SELECT pd.pd_fecha, pd.pd_distribuidora_ed, pd.pd_pedido_realizado, pdpi.pdpi_pedido_item_pi
FROM pdpi_pedido_distribuidora_item pdpi
JOIN pd_pedido_a_distribuidora pd ON pd.pd_pedido_a_distribuidora_k = pdpi.pdpi_pedido_a_distribuidora_pd
WHERE pdpi.pdpi_pedido_item_pi != pd.pd_pedido_item_pi;

-- Step 4: Drop the join table
DROP TABLE pdpi_pedido_distribuidora_item;

-- Step 5: Add FK constraint
ALTER TABLE pd_pedido_a_distribuidora
ADD CONSTRAINT fk_pd_pedido_item FOREIGN KEY (pd_pedido_item_pi) REFERENCES pi_pedido_item(pi_pedido_item_k);
