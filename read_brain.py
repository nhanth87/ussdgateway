import json

with open('brain_export.json', 'r', encoding='utf-8') as f:
    data = json.load(f)

# Build neuron id -> content map
neurons = {n['id']: n['content'] for n in data['neurons']}

# Build fiber id -> combined content
fibers = {}
for fib in data['fibers']:
    fiber_id = fib['id']
    content = ' '.join([neurons.get(nid, '') for nid in fib['neuron_ids']])
    fibers[fiber_id] = content

# Print typed memories with content
print("=" * 80)
print("NEURAL MEMORY RECALL - ETHIOPIA PROJECT")
print("=" * 80)
print()

typed_memories = data.get('metadata', {}).get('typed_memories', [])
print(f"Total memories: {len(typed_memories)}")
print()

for tm in typed_memories:
    fid = tm['fiber_id']
    content = fibers.get(fid, 'N/A')
    
    print(f"=== [{tm['memory_type'].upper()}] Priority: {tm['priority']} ===")
    print(f"Tags: {', '.join(tm['tags'])}")
    print(f"Created: {tm['created_at'][:10]}")
    print(f"Content: {content[:500]}")
    print()
